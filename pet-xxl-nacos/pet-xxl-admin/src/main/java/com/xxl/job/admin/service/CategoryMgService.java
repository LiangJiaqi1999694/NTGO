package com.xxl.job.admin.service;

import com.xxl.job.core.biz.model.MenuCategory;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * 
 * @describe 编目管理
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月8日 下午4:50:00
 *
 */
public interface CategoryMgService {

	/**
	 * 新建编目
	 * 
	 * @param name     编目名称
	 * @param type     编目类型
	 * @param pre_id   同级前驱节点id
	 * @param next_id  同级后驱节点id
	 * @param parentId 父级id
	 * @return
	 */
	public ReturnT<String> createCategory(String name, Integer type,Long parentId);

	/**
	 * 
	 * @param id       节点id
	 * @param name     编目名称
	 * @param pre_id   同级前驱节点id
	 * @param next_id  同级后驱节点id
	 * @param parentId 父级id
	 * @return
	 */
	public ReturnT<String> updateCategory(Long id, String name,  Long parentId);

	/**
	 * query
	 * 
	 * @return
	 */
	public ReturnT<List<MenuCategory>> selectallcategorys(Integer type);

	/**
	 * query
	 * 
	 * @return
	 */
	public List<Long> selectcategoryById(Long id);

	/**
	 * delete
	 * 
	 * @param id
	 * @return
	 */
	public ReturnT<String> deletecategory(Long id);
}
