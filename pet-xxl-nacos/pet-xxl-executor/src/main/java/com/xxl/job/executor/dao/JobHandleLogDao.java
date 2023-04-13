package com.xxl.job.executor.dao;


import com.xxl.job.core.biz.model.JobHandleLog;
import org.apache.ibatis.annotations.Mapper;


/**
 * 
 * @describe 子任务日志
 *
 *
 */
@Mapper
public interface JobHandleLogDao {
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public JobHandleLog selectById(Long id);
	
	/**
	 * 插入
	 * @param JobHandleLog
	 * @return
	 */
	public int insert(JobHandleLog JobHandleLog);
	
	/**
	 * 更新
	 * @param JobHandleLog
	 * @return
	 */
	public int update(JobHandleLog JobHandleLog);
	
}
