package com.xxl.job.admin.dao;

import com.xxl.job.core.biz.model.JobHandleLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 
 * @describe handle 任务日志
 *
 * @version 创建时间：2020年6月17日 下午4:16:47
 *
 */
@Mapper
public interface JobHandleLogDao {
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public JobHandleLog selectById(Long id);
	
	/**
	 * 
	 * @return
	 */
	public List<JobHandleLog> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("logId") int logId, @Param("logStatus") int logStatus);

	/**
	 * 统计
	 * @param offset
	 * @param pagesize
	 * @param logId
	 * @param logStatus
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Integer pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("logId") int logId, @Param("logStatus") int logStatus);

	/**
	 * 根据logid清理日志
	 * @param logIds
	 * @return
	 */
	public Integer clearLog(@Param("logIds") List<Long> logIds);
	
	/**
	 * 根据logid清理日志
	 * @param logIds
	 * @return
	 */
	public Integer deleteLog(@Param("logId") Long logId);

	/**
	 * @描述 任务耗时
	 * @author 庄勇
	 * @date 13:55 2020/10/15 0015
	 * @param
	 * @return
	 * @修改人和其他信息
	 **/
    List<Map<String,Object>> groupByTimeHandle();
}
