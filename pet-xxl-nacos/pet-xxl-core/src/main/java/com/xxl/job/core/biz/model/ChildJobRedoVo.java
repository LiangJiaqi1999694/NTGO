package com.xxl.job.core.biz.model;

import java.util.List;

/**
 *
 *@describe 子任务重做
 *
 *@author zy
 *
 *@date 2020-11-17 17:55:42
 *
 */
public class ChildJobRedoVo {

	private Long handleLogId;

	private List<JobParam> jobParams;

	public Long getHandleLogId() {
		return handleLogId;
	}

	public List<JobParam> getJobParams() {
		return jobParams;
	}

	public void setHandleLogId(Long handleLogId) {
		this.handleLogId = handleLogId;
	}

	public void setJobParams(List<JobParam> jobParams) {
		this.jobParams = jobParams;
	}
}
