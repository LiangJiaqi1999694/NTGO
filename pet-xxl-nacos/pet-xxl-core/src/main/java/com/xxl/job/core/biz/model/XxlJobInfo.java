package com.xxl.job.core.biz.model;

import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {

	private int id;				// 主键ID
	private Long algoId;//算法id
	private String algoName;//算法名称

	private int jobGroup;		// 执行器主键ID
	private String jobCron;		// 任务执行CRON表达式
	private String jobDesc;


	private Long handleLogId;//子任务日志id

	private String jobhandler;//jobhandler

	private Date addTime;
	private Date updateTime;
	private Long categoryId;



	private boolean isRedo;//是否重做

	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件

	private String executorRouteStrategy;	// 执行器路由策略
	private String executorHandler;		    // 执行器，任务Handler名称
	private String executorParam;		    // 执行器，任务参数
	private String executorBlockStrategy = ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name();	// 阻塞处理策略
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	private int executorFailRetryCount;		// 失败重试次数

	private String glueType = GlueTypeEnum.BEAN.toString();
	// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注
	private Date glueUpdatetime;	// GLUE更新时间

	private String childJobId;		// 子任务ID，多个逗号分隔
	private List<JobParam> jobParams;//任务参数

	private int triggerStatus;		// 调度状态：0-停止，1-运行
	private long triggerLastTime;	// 上次调度时间
	private long triggerNextTime;	// 下次调度时间
	private Integer jobType;//任务类型：1、数据汇集 2、数据预处理 3、数据归档、4、产品生产 5、其他
	public Long getHandleLogId() {
		return handleLogId;
	}

	public void setHandleLogId(Long handleLogId) {
		this.handleLogId = handleLogId;
	}


	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}

	public String getParamValue(String paramName) {
		if(!CollectionUtils.isEmpty(jobParams)) {
			for(int i=0;i<jobParams.size();i++) {
				if(jobParams.get(i).getParamName().equalsIgnoreCase(paramName)) {
					return jobParams.get(i).getParamValue();
				}
			}
		}
		return null;
	}

	public List<JobParam> getJobParams() {
		return jobParams;
	}

	public void setJobParams(List<JobParam> jobParams) {
		this.jobParams = jobParams;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isRedo() {
		return isRedo;
	}

	public void setRedo(boolean redo) {
		isRedo = redo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobGroup() {
		return jobGroup;
	}

	public String getJobhandler() {
		return jobhandler;
	}

	public String getAlgoName() {
		return algoName;
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}
	public void setJobhandler(String jobhandler) {
		this.jobhandler = jobhandler;
	}
	public void setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobCron() {
		return jobCron;
	}

	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}

	public Long getAlgoId() {
		return algoId;
	}

	public void setAlgoId(Long algoId) {
		this.algoId = algoId;
	}
	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlarmEmail() {
		return alarmEmail;
	}

	public void setAlarmEmail(String alarmEmail) {
		this.alarmEmail = alarmEmail;
	}

	public String getExecutorRouteStrategy() {
		return executorRouteStrategy;
	}

	public void setExecutorRouteStrategy(String executorRouteStrategy) {
		this.executorRouteStrategy = executorRouteStrategy;
	}

	public String getExecutorHandler() {
		return executorHandler;
	}

	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}

	public String getExecutorParam() {
		return executorParam;
	}

	public void setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
	}

	public String getExecutorBlockStrategy() {
		return executorBlockStrategy;
	}

	public void setExecutorBlockStrategy(String executorBlockStrategy) {
		this.executorBlockStrategy = executorBlockStrategy;
	}

	public int getExecutorTimeout() {
		return executorTimeout;
	}

	public void setExecutorTimeout(int executorTimeout) {
		this.executorTimeout = executorTimeout;
	}

	public int getExecutorFailRetryCount() {
		return executorFailRetryCount;
	}

	public void setExecutorFailRetryCount(int executorFailRetryCount) {
		this.executorFailRetryCount = executorFailRetryCount;
	}

	public String getGlueType() {
		return glueType;
	}

	public void setGlueType(String glueType) {
		this.glueType = glueType;
	}

	public String getGlueSource() {
		return glueSource;
	}

	public void setGlueSource(String glueSource) {
		this.glueSource = glueSource;
	}

	public String getGlueRemark() {
		return glueRemark;
	}

	public void setGlueRemark(String glueRemark) {
		this.glueRemark = glueRemark;
	}

	public Date getGlueUpdatetime() {
		return glueUpdatetime;
	}

	public void setGlueUpdatetime(Date glueUpdatetime) {
		this.glueUpdatetime = glueUpdatetime;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public int getTriggerStatus() {
		return triggerStatus;
	}

	public void setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
	}

	public long getTriggerLastTime() {
		return triggerLastTime;
	}

	public void setTriggerLastTime(long triggerLastTime) {
		this.triggerLastTime = triggerLastTime;
	}

	public long getTriggerNextTime() {
		return triggerNextTime;
	}

	public void setTriggerNextTime(long triggerNextTime) {
		this.triggerNextTime = triggerNextTime;
	}

	public boolean isShardingRouteStrategy() {
		return "SHARDING_BROADCAST".equalsIgnoreCase(executorRouteStrategy);
	}
}
