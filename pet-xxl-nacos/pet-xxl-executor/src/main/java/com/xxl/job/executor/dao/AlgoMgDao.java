package com.xxl.job.executor.dao;


import com.xxl.job.core.biz.model.Algorithm;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AlgoMgDao {


	/**
	 * 根据id查询算法信息
	 * 
	 * @param id
	 * @return
	 */
	public Algorithm selectAlgosById(Long id);

}
