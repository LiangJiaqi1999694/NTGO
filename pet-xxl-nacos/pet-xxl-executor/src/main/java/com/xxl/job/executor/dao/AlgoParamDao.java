package com.xxl.job.executor.dao;

import com.xxl.job.core.biz.model.AlgorithmParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlgoParamDao {

	public List<AlgorithmParam> selectParamsByAlgoId(@Param("algoId") Long algoId);

}
