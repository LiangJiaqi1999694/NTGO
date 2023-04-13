package com.xxl.job.executor.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import com.xxl.job.executor.config.XxlJobSampleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @describe 基础任务处理类，所有业务插件需要继承该类
 *
 * @version 创建时间：2020年6月9日 上午11:49:50
 *
 */
public abstract class BaseJobHandler<T> extends IJobHandler {

	private Logger logger = LoggerFactory.getLogger(BaseJobHandler.class);
	
	
	@Override
	public final ReturnT<String> execute(TriggerParam triggerParam) throws Exception {

		if (ObjectUtils.isEmpty(triggerParam)) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "非法参数！");
		}

		long jobId = triggerParam.getJobId();

		if (jobId <= 0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, jobId + "：非法任务id！");
		}
		XxlJobInfo xxlJobInfo = loadJobInfo(jobId,triggerParam);
		xxlJobInfo.setRedo(triggerParam.isRedo());
		if (ObjectUtils.isEmpty(xxlJobInfo)) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, jobId + "：非法任务id！");
		}

		if (ObjectUtils.isEmpty(xxlJobInfo.getAlgoId())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, jobId + "：未绑定插件！");
		}
		// get childJobs
		final List<T> childJobs = new ArrayList<>();
		if(triggerParam.isRedo()) {
			Long handleLogId = triggerParam.getRedoHandleLogId();
			if(!ObjectUtils.isEmpty(handleLogId)) {
				JobHandleLog jobHandleLog = XxlJobSampleConfig.getSampleConfig().getJobHandleLogDao().selectById(handleLogId);
				if(!ObjectUtils.isEmpty(jobHandleLog)) {
					@SuppressWarnings("unchecked")
					T t = JSONObject.parseObject(jobHandleLog.getExecuteParam(),(Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
					childJobs.add(t);
				}
			}
		}else {
			List<T> cl = getChildJobs(xxlJobInfo);
			if(!CollectionUtils.isEmpty(cl)) {
				childJobs.addAll(cl);
			}
		}
		
		if (CollectionUtils.isEmpty(childJobs)) {
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, "数据为空！");
		}

		// 分片参数
		ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
		if (xxlJobInfo.isShardingRouteStrategy()) {// 分片
			XxlJobLogger.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardingVO.getIndex(), shardingVO.getTotal());
			logger.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardingVO.getIndex(), shardingVO.getTotal());
		}
		
		int executorFailRetryCount = xxlJobInfo.getExecutorFailRetryCount();
		int retryCount = 0;
		for (int i = 0; i < childJobs.size();i++) {
			if (xxlJobInfo.isShardingRouteStrategy() &&shardingVO.getTotal()>0&& !(i % shardingVO.getTotal() == (shardingVO.getIndex()))) {
				continue;
			}

			if (xxlJobInfo.isShardingRouteStrategy()) {
				XxlJobLogger.log("第 {} 片, 命中分片开始处理第 {} 子任务", shardingVO.getIndex(), i);
				logger.info("第 {} 片, 命中分片开始处理第 {} 子任务", shardingVO.getIndex(), i);
			}

			TimeUnit.MILLISECONDS.sleep(1);

			// job log insert
			Date startDate = new Date();
			JobHandleLog handleLog = new JobHandleLog();
			handleLog.setHandleStartTime(startDate);
			handleLog.setExecuteParam(JSONObject.toJSONString(childJobs.get(i)));
			handleLog.setLogId(triggerParam.getLogId());
			handleLog.setAlgoId(xxlJobInfo.getAlgoId());
			XxlJobSampleConfig.getSampleConfig().getJobHandleLogDao().insert(handleLog);
			//set joblogId
			xxlJobInfo.setHandleLogId(handleLog.getId());
			
			ReturnT<String> executeResult = null;

			if (triggerParam.getExecutorTimeout() > 0) {
				// limit timeout
				Thread futureThread = null;
				try {
					final int iTmp = i;
					FutureTask<ReturnT<String>> futureTask = new FutureTask<ReturnT<String>>(
							new Callable<ReturnT<String>>() {
								@Override
								public ReturnT<String> call() throws Exception {
									return processChildJobs(xxlJobInfo,childJobs.get(iTmp));
								}
							});
					futureThread = new Thread(futureTask);
					futureThread.start();
					executeResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
				} catch (TimeoutException e) {
					XxlJobLogger.log("<br>----------- xxl-job job execute timeout");
					XxlJobLogger.log(e);
					executeResult = new ReturnT<String>(IJobHandler.FAIL_TIMEOUT.getCode(), "job execute timeout ");
				} finally {
					futureThread.interrupt();
				}

			} else {
				executeResult = processChildJobs(xxlJobInfo,childJobs.get(i));
			}

			if (executorFailRetryCount > 0 && executeResult.getCode() != IJobHandler.SUCCESS.getCode()&&retryCount<executorFailRetryCount) {// 失败重试
				i--;
				retryCount++;
				XxlJobLogger.log("失败重试，第{}次", retryCount);
				logger.info("失败重试，第{}次", retryCount);
				continue;
			}
			
			
			if(executorFailRetryCount>0) {
				retryCount =0;
			}
			
			Date endDate = new Date();
			// job log update
			handleLog.setHandleEndTime(endDate);
			handleLog.setHandleCode(executeResult.getCode());
			handleLog.setHandleMsg(executeResult.getMsg());
			handleLog.setHandleCostTime(endDate.getTime()-startDate.getTime());
			XxlJobSampleConfig.getSampleConfig().getJobHandleLogDao().update(handleLog);
		}
		
		return ReturnT.SUCCESS;
	}

	/**
	 * 根据任务参数获取子任务列表
	 * 
	 * @param xxlJobInfo
	 * @return
	 */
	public abstract List<T> getChildJobs(XxlJobInfo xxlJobInfo);

	/**
	 * 处理子任务信息
	 * 
	 * @param t
	 * @return
	 */
	public abstract ReturnT<String> processChildJobs(XxlJobInfo xxlJobInfo, T t);
	
	
	/**
	 * 根据jobId加载算法信息
	 * 
	 * @param jobId
	 * @return
	 */
	private XxlJobInfo loadJobInfo(long jobId,TriggerParam triggerParam) {
		XxlJobInfo xxlJobInfo = XxlJobSampleConfig.getSampleConfig().getXxlJobInfoDao().loadById(jobId);
		if (!ObjectUtils.isEmpty(xxlJobInfo) && !ObjectUtils.isEmpty(xxlJobInfo.getAlgoId())) {
			List<AlgorithmParam> algorithmParams = XxlJobSampleConfig.getSampleConfig().getAlgoParamDao()
					.selectParamsByAlgoId(xxlJobInfo.getAlgoId());
			List<JobParam> jobParams = XxlJobSampleConfig.getSampleConfig().getJobParamDao().selectByJobId(jobId);
			setJobParams(xxlJobInfo, algorithmParams, jobParams);
			if(triggerParam.isRedo()) {
				setRedoJobParams(xxlJobInfo, triggerParam.getRedoParams());
			}
		}
		return xxlJobInfo;
	}

	/**
	 * 加载任务信息
	 * 
	 * @param xxlJobInfo
	 * @param algorithmParams 算法参数
	 * @param jobParams       任务参数
	 */
	private void setJobParams(XxlJobInfo xxlJobInfo, List<AlgorithmParam> algorithmParams, List<JobParam> jobParams) {
		if (!CollectionUtils.isEmpty(algorithmParams)) {
			List<JobParam> params = new ArrayList<>();
			boolean isExist = false;
			for (int i = 0; i < algorithmParams.size(); i++) {
				isExist = false;
				JobParam jobParam = null;
				if (!CollectionUtils.isEmpty(jobParams)) {
					for (int j = 0; j < jobParams.size(); j++) {
						if (algorithmParams.get(i).getId().equals(jobParams.get(j).getParamId())) {
							jobParam = jobParams.get(j);
							if(!ObjectUtils.isEmpty(jobParams.get(j).getId())) {
								isExist = true;
							}
							break;
						}
					}
				}
				if (!isExist) {
					jobParam = new JobParam();
					jobParam.setParamValue(algorithmParams.get(i).getDefaultValue());
				}
				Integer id = xxlJobInfo.getId();
				jobParam.setJobId(id.longValue());
                jobParam.setParamId(algorithmParams.get(i).getId());
                jobParam.setParamType(algorithmParams.get(i).getParamType());
                jobParam.setParamName(algorithmParams.get(i).getParamName());
                jobParam.setParamDescription(algorithmParams.get(i).getParamDescription());
                jobParam.setDictionaryCategoryId(algorithmParams.get(i).getDictionaryCategoryId());
                jobParam.setIsshow(algorithmParams.get(i).getIsShow());
                jobParam.setIsredo(algorithmParams.get(i).getIsRedo());
				params.add(jobParam);
			}
			xxlJobInfo.setJobParams(params);
		}
	}
	
	/**
	 * 加载重做参数
	 * @param xxlJobInfo
	 * @param redoJobParams
	 */
	private void setRedoJobParams(XxlJobInfo xxlJobInfo,List<JobParam> redoJobParams) {
		if(!CollectionUtils.isEmpty(redoJobParams)&&!CollectionUtils.isEmpty(xxlJobInfo.getJobParams())) {
			for(int i=0;i<redoJobParams.size();i++) {
				for(int j=0;j<xxlJobInfo.getJobParams().size();j++) {
					if(xxlJobInfo.getJobParams().get(j).getParamId().equals(redoJobParams.get(i).getParamId())) {
						if(xxlJobInfo.getJobParams().get(j).isRedo()) {
							xxlJobInfo.getJobParams().get(j).setParamValue(redoJobParams.get(i).getParamValue());
						}
						break;
					}
				}
			}
		}
	}
}
