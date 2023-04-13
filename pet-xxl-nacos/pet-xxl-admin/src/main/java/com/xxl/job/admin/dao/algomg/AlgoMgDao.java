package com.xxl.job.admin.dao.algomg;

import com.xxl.job.core.biz.model.Algorithm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 
 * @describe 算法管理 dao
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月19日 下午3:22:46
 *
 */
@Mapper
public interface AlgoMgDao {

	/**
	 * 创建算法
	 * 
	 * @param algorithm
	 * @return
	 */
	public boolean createAlgo(Algorithm algorithm);

	/**
	 * 查询算法
	 * @param categoryIds
	 * @param fuzzySearchName
	 * @return
	 */
	public List<Algorithm> selectAlgos(@Param("categoryIds") List<Long> categoryIds, @Param("fuzzySearchName") String fuzzySearchName);


	/**
	 * 查询全部算法
	 * @return
	 */
	public List<Algorithm> selectAllAlgos();

	/**
	 * 根据id查询算法信息
	 *
	 * @param id
	 * @return
	 */
	public Algorithm selectAlgosById(Long id);

	/**
	 * 根据ids查询算法信息
	 *
	 * @param id
	 * @return
	 */
	public List<Algorithm> selectAlgosByIds(@Param("ids") List<Long> ids);


	/**
	 * 根据jobhandler查询算法id
	 * 
	 * @param id
	 * @return
	 */
	public Algorithm selectAlgosIdByJobhandler(String jobhandler);

	/**
	 * 更新算法信息
	 * 
	 * @param algorithm
	 * @return
	 */
	public boolean updateAlgosById(Algorithm algorithm);

	/**
	 * 根据id 删除算法
	 * 
	 * @param id 算法id
	 * @return
	 */
	public boolean deleteAlgo(Long id);

	/**
	 * 根据id 批量删除算法
	 * 
	 * @param id 算法id
	 * @return
	 */
	public boolean batchDeleteAlgo(Long[] ids);
	
	/**
	 * 查询所属分组的算法id
	 * @param categoryId
	 * @return
	 */
	public List<Long> selectIdsByCategoryId(Long categoryId);

}
