package com.xxl.job.admin.dao.algomg;

import com.xxl.job.core.biz.model.AlgorithmParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 
 * @describe 算法参数 dao
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月20日 下午4:55:27
 *
 */
@Mapper
public interface AlgoParamDao {

	public List<AlgorithmParam> selectParamsByAlgoId(@Param("algoId") Long algoId);

	public boolean addParam(AlgorithmParam algorithmParam);

	public boolean batchaddParam(List<AlgorithmParam> algorithmParam);

	public boolean updateParam(AlgorithmParam algorithmParam);

	public boolean deleteParam(Long id);
	
	public boolean deleteParamByAlgoId(Long algoId);
	
	public boolean batchDeleteParamByIds(Long[] ids);

	public boolean batchDeleteParamByAlgoId(Long[] algoIds);

}
