package com.xxl.job.core.thread;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.*;


/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class JobThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);

	private long jobId;
	private IJobHandler handler;
	private TriggerParam currentTriggerParam;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private Set<Long> triggerLogIdSet; // avoid repeat trigger for the same TRIGGER_LOG_ID

	private volatile boolean toStop = false;
	private String stopReason;

	private boolean running = false; // if running job
	private int idleTimes = 0; // idel times

	public JobThread(long jobId, IJobHandler handler) {
		this.jobId = jobId;
		this.handler = handler;
		this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Long>());
	}

	public IJobHandler getHandler() {
		return handler;
	}

	/**
	 * new trigger to queue
	 *
	 * @param triggerParam
	 * @return
	 */
	public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
		// avoid repeat
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
		return ReturnT.SUCCESS;
	}

	/**
	 * kill job thread
	 *
	 * @param stopReason
	 */
	public void toStop(String stopReason) {
		/**
		 * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
		 * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身； 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
		 */
		this.toStop = true;
		this.stopReason = stopReason;
	}

	/**
	 * is running job
	 *
	 * @return
	 */
	public boolean isRunningOrHasQueue() {
		return running || triggerQueue.size() > 0;
	}

	/**
	 * is running jobQueue size
	 *
	 * @return
	 */
	public int jobQueueSize() {
		return triggerQueue.size();
	}

	/**
	 *
	 * @param logId
	 * @return
	 */
	public boolean removeTriggerQueue(long logId) {

		if (!ObjectUtils.isEmpty(currentTriggerParam)) {
			if (currentTriggerParam.getLogId() == logId) {
				currentTriggerParam = null;
				interrupt();
				return true;
			}
		}

		if (triggerQueue.size() > 0) {
			Iterator<TriggerParam> iterator = triggerQueue.iterator();
			while (iterator.hasNext()) {
				TriggerParam triggerParam = iterator.next();
				if (triggerParam.getLogId() == logId) {
					iterator.remove();
					triggerLogIdSet.remove(logId);
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void run() {

		// init
		try {
			handler.init();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		// execute
		while (!toStop) {
			running = false;
			idleTimes++;

			TriggerParam triggerParam = null;
			ReturnT<String> executeResult = null;
			try {
				// to check toStop signal, we need cycle, so wo cannot use queue.take(), instand
				// of poll(timeout)
				triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam != null) {

					currentTriggerParam = triggerParam;
					running = true;
					idleTimes = 0;
					triggerLogIdSet.remove(triggerParam.getLogId());

					// log filename, like "logPath/yyyy-MM-dd/9999.log"
					String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTime()),
							triggerParam.getLogId());
					XxlJobFileAppender.contextHolder.set(logFileName);
					ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(),
							triggerParam.getBroadcastTotal()));

					// update execute time
					TriggerCallbackThread.pushCallBack(
							new HandleCallbackParam(triggerParam.getLogId(), System.currentTimeMillis(), null));


					//队列超时：在队列时间超过2h && 目前队列长度>10
					if(((System.currentTimeMillis()-triggerParam.getLogDateTime())>2*60*60*1000)
							&&triggerQueue.size()>10){
						String errMsg = triggerParam.toString()+ " ：在队列等待时长超过1天，未执行";
						XxlJobLogger.log(errMsg);
						executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errMsg);
						continue;
					}

					// execute
					XxlJobLogger.log("<br>----------- xxl-job job execute start -----------<br>----------- jobId:"
							+ triggerParam.getJobId());

					executeResult = handler.execute(triggerParam);

					if (executeResult == null) {
						executeResult = IJobHandler.FAIL;
					} else {
						executeResult.setMsg((executeResult != null && executeResult.getMsg() != null
								&& executeResult.getMsg().length() > 50000)
								? executeResult.getMsg().substring(0, 50000).concat("...")
								: executeResult.getMsg());
						executeResult.setContent(null); // limit obj size
					}
					XxlJobLogger
							.log("<br>----------- xxl-job job execute end(finish) -----------<br>----------- ReturnT:"
									+ executeResult);

				} else {
					if (idleTimes > 30) {
						if (triggerQueue.size() == 0) { // avoid concurrent trigger causes jobId-lost
							XxlJobExecutor.removeJobThread((int)jobId, "excutor idel times over limit.");
						}
					}
				}
			} catch (Throwable e) {
				if (toStop) {
					XxlJobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
				}

				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String errorMsg = stringWriter.toString();
				executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

				XxlJobLogger.log("<br>----------- JobThread Exception:" + errorMsg
						+ "<br>----------- xxl-job job execute end(error) -----------");
			} finally {
				if (triggerParam != null) {
					// callback handler info
					if (!toStop) {
						// commonm
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),
								triggerParam.getLogDateTime(), executeResult));
					} else {
						// is killed
						ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,
								stopReason + " [job running, killed]");
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),
								triggerParam.getLogDateTime(), stopResult));
					}
				}
			}
		}

		// callback trigger request in queue
		while (triggerQueue != null && triggerQueue.size() > 0) {
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam != null) {
				// is killed
				ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,
						stopReason + " [job not executed, in the job queue, killed.]");
				TriggerCallbackThread.pushCallBack(
						new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTime(), stopResult));
			}
		}

		// destroy
		try {
			handler.destroy();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
	}
}
