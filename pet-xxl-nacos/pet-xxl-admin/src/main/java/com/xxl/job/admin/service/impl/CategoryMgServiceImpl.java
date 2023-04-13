package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.dao.CategoryMgDao;
import com.xxl.job.admin.service.CategoryMgService;
import com.xxl.job.core.biz.model.MenuCategory;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CategoryMgServiceImpl implements CategoryMgService {

	@Autowired
	private CategoryMgDao categoryMgDao;
	
//	@Autowired
//	private DataDictionaryDao dataDictionaryDao;
//
//
//	@Autowired
//	private AlgoMgDao algoMgDao;
//
	private final Integer SORTID_STEP = 50;
	
	

	@Override
	public ReturnT<String> createCategory(String name, Integer type,
                                          Long parentId) {
		if (StringUtils.isEmpty(name)) {
			return ReturnT.fail("编目名称不能为空！");
		}

		if (name.length() > 50) {
			return ReturnT.fail("编目名称长度不能大于50！");
		}

		MenuCategory menuCategory = new MenuCategory();
		Date now = new Date();
		menuCategory.setCreateTime(now);
		menuCategory.setUpdateTime(now);
		menuCategory.setName(name);
		menuCategory.setParentId(parentId);
		menuCategory.setType(type);
		menuCategory.setSortId(0);

		if (categoryMgDao.addMenuCategory(menuCategory)) {
			return ReturnT.ok("新增编目成功！");
		} else {
			return ReturnT.fail("新增编目失败！");
		}

	}

	@Override
	public ReturnT<String> updateCategory(Long id, String name, Long parentId) {
		if (ObjectUtils.isEmpty(id)) {
			return ReturnT.fail("节点id不能为空！");
		}

		if (!StringUtils.isEmpty(name) && name.length() > 50) {
			return ReturnT.fail("编目名称长度不能大于50！");
		}

		MenuCategory menuCategory = categoryMgDao.selectMenuCategoryById(id);

		if (ObjectUtils.isEmpty(menuCategory)) {
			return ReturnT.fail("节点不存在！");
		}

		// 更新
		if (!StringUtils.isEmpty(name)) {
			menuCategory.setName(name);
		}

		menuCategory.setUpdateTime(new Date());
		if (!ObjectUtils.isEmpty(parentId)) {
			menuCategory.setParentId(parentId);
		}
		menuCategory.setSortId(0);

		if (categoryMgDao.updateCategory(menuCategory)) {
			return ReturnT.ok("更新成功！");
		} else {
			return ReturnT.fail("更新失败！");
		}
	}


	@Override
	public ReturnT<List<MenuCategory>> selectallcategorys(Integer type) {
		List<MenuCategory> menuCategorys = categoryMgDao.selectMenuCategorysByParentId(0L, type);
		if (CollectionUtils.isEmpty(menuCategorys)) {
			return new ReturnT<List<MenuCategory>>(new ArrayList<MenuCategory>());
		}
		for (MenuCategory menuCategory : menuCategorys) {
			setSonCategorys(menuCategory);
		}
		return new ReturnT<List<MenuCategory>>(menuCategorys);
	}

	@Override
	public List<Long> selectcategoryById(Long id) {
		if (!ObjectUtils.isEmpty(id)) {
			MenuCategory menuCategory = categoryMgDao.selectMenuCategoryById(id);
			if(!ObjectUtils.isEmpty(menuCategory)) {
				setSonCategorys(menuCategory);
				return menuCategory.getids();
			}
		}
		return null;
	}

	private void setSonCategorys(MenuCategory menuCategory) {
		if (!ObjectUtils.isEmpty(menuCategory)) {
			List<MenuCategory> sonMenuCategorys = categoryMgDao.selectMenuCategorysByParentId(menuCategory.getId(),
					menuCategory.getType());
			if (!CollectionUtils.isEmpty(sonMenuCategorys)) {
				menuCategory.setSonMenuCategorys(sonMenuCategorys);
				for (MenuCategory sonMenuCategory : sonMenuCategorys) {
					setSonCategorys(sonMenuCategory);
				}
			}
		}
	}

	@Override
	@Transactional
	public ReturnT<String> deletecategory(Long id) {
		// TODO Auto-generated method stub
		if (ObjectUtils.isEmpty(id)) {
			return ReturnT.fail("编目id不能为空！");
		}

		MenuCategory menu = categoryMgDao.selectMenuCategoryById(id);

		if(ObjectUtils.isEmpty(menu)) {
			return ReturnT.fail("编目信息不存在！");
		}

//		setSonCategorys(menu);
//
//		List<Long> ids = menu.getids();
//		MenuCategory sonmenu = null;
//		if(!CollectionUtils.isEmpty(ids)) {
//			for(int i=0;i<ids.size();i++) {
//				sonmenu = categoryMgDao.selectMenuCategoryById(ids.get(i));
//				if(MenuCategory.DATADICTIONARY_TYPE.equals(menu.getType())) {
//					List<Long> dataDictionaryIds = dataDictionaryDao.selectIdsByCategoryId(id);
//					if(!CollectionUtils.isEmpty(dataDictionaryIds)) {
//						return ReturnT.fail("删除失败，请先删除分组下面的字典！");
//					}
//				}
//
//				if(MenuCategory.ALGO_TYPE.equals(menu.getType())) {
//					List<Long> algoIds = algoMgDao.selectIdsByCategoryId(id);
//					if(!CollectionUtils.isEmpty(algoIds)) {
//						return ReturnT.fail("删除失败，请先删除分组下面的算法！");
//					}
//				}
//			}
//		}
//

		if (categoryMgDao.deleteCategory(id)) {
			return ReturnT.ok("删除成功！");
		} else {
			return ReturnT.fail("删除失败！");
		}

	}

}
