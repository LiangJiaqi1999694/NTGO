package com.xxl.job.core.biz.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 
 * @describe 叶子任务信息
 *
 * @author zhd
 *
 * @version 创建时间：2020年6月9日 下午5:26:36
 *
 */
public class JobHandleLog{
	
	//算法id
	private Long algoId;
	
	//执行参数
	private String executeParam;
	
	//执行状态
	private Integer handleCode;
	
	//执行耗时
	private Long handleCostTime;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date handleEndTime;
	
	//执行信息
	private String handleMsg;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date handleStartTime;
	
	//id
	private long id;
	
	//日志id
	private long logId;

	public Long getAlgoId() {
		return algoId;
	}

	public String getExecuteParam() {
		return executeParam;
	}

	public Integer getHandleCode() {
		return handleCode;
	}

	public Long getHandleCostTime() {
		return handleCostTime;
	}

	public Date getHandleEndTime() {
		return handleEndTime;
	}

	public String getHandleMsg() {
		return handleMsg;
	}

	public Date getHandleStartTime() {
		return handleStartTime;
	}

	public long getId() {
		return id;
	}

	public long getLogId() {
		return logId;
	}

	public void setAlgoId(Long algoId) {
		this.algoId = algoId;
	}

	public void setExecuteParam(String executeParam) {
		this.executeParam = executeParam;
	}

	public void setHandleCode(Integer handleCode) {
		this.handleCode = handleCode;
	}

	public void setHandleCostTime(Long handleCostTime) {
		this.handleCostTime = handleCostTime;
	}

	public void setHandleEndTime(Date handleEndTime) {
		this.handleEndTime = handleEndTime;
	}

	public void setHandleMsg(String handleMsg) {
		this.handleMsg = handleMsg;
	}

	public void setHandleStartTime(Date handleStartTime) {
		this.handleStartTime = handleStartTime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}
	
	@Override
	public String toString() {
		return "JobHandleLog [executeParam=" + executeParam + ", handleCode=" + handleCode + ", handleMsg=" + handleMsg
				+ ", id=" + id + ", logId=" + logId + ", handleStartTime=" + handleStartTime + ", handleEndTime="
				+ handleEndTime + "]";
	}
	
}
