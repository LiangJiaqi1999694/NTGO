package com.xxl.job.admin.dao;

import com.xxl.job.core.biz.model.JobParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 
 * @describe 描述
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月26日 下午2:23:30
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
	
	/**
	 * 批量插入
	 * @param jobParams
	 * @return
	 */
	public int batchInsertParams(List<JobParam> jobParams);
	
	/**
	 * 更新
	 * @param jobParam
	 * @return
	 */
	public boolean updateParams(JobParam jobParam);
	
	/**
	 * 根据paramId批量删除
	 * @param paramIds
	 * @return
	 */
	public int deleteByParamIds(Long[] paramIds);
	
	/**
	 * 根据jobId批量删除
	 * @param jobId
	 * @return
	 */
	public int deleteByJobId(long jobId);
}
