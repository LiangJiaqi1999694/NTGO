package com.xxl.job.executor.dao;

import com.xxl.job.core.biz.model.JobParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 
 * @describe 描述
 *
 *
 */
@Mapper
public interface JobParamDao {
	
	/**
	 * 根据任务检索任务参数
	 * @param jobId
	 * @return
	 */
	public List<JobParam> selectByJobId(long jobId);
}
