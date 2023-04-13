package com.xxl.job.executor.dao;


import com.xxl.job.core.biz.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * job info
 * 
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

	public XxlJobInfo loadById(@Param("id") long id);
	
}
