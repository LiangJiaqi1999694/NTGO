package com.xxl.job.admin.service;

import com.github.pagehelper.PageInfo;
import com.xxl.job.core.biz.model.Algorithm;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * 
 * @describe 算法管理 Service
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月19日 下午2:59:25
 *
 */
public interface AlgoMgService {

	/**
	 * 保存算法
	 * 
	 * @param algorithm
	 * @return
	 */
	public ReturnT<Algorithm> saveAlgo(Algorithm algorithm);

	/**
	 * 更新算法
	 * 
	 * @param algorithm
	 * @return
	 */
	public ReturnT<String> updateAlgo(Algorithm algorithm);

	/**
	 * 查询算法
	 * 
	 * @param categoryId
	 * @param fuzzySearchName
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ReturnT<PageInfo<Algorithm>> selectAlgos(Long categoryId, String fuzzySearchName, Integer pageNum, Integer pageSize);

	/**
	 * 根据id查询算法
	 * 
	 * @param id
	 * @return
	 */
	public ReturnT<Algorithm> selectAlgosById(Long id);

	/**
	 * 删除算法
	 * 
	 * @param id
	 * @return
	 */
	public ReturnT<String> deleteAlgo(Long id);

	/**
	 * 批量删除算法
	 * 
	 * @param id
	 * @return
	 */
	public ReturnT<String> batchDeleteAlgo(String[] ids);

}
