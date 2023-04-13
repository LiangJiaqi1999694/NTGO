package com.xxl.job.admin.dao;

import com.xxl.job.core.biz.model.MenuCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * @describe 编目管理 dao
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月15日 上午10:26:33
 *
 */
@Mapper
public interface CategoryMgDao {

	public MenuCategory selectMenuCategoryById(Long id);

	public Double selectMaxSortIdByParentId(Long parentId);

	public List<MenuCategory> selectMenuCategorysByParentId(@Param("parentId") Long parentId,
                                                            @Param("type") Integer type);

	public boolean addMenuCategory(MenuCategory menuCategory);

	public boolean updateCategory(MenuCategory menuCategory);
	
	public boolean deleteCategory(Long id);
	
	public boolean deleteCategorys(List<Long> id);

}
