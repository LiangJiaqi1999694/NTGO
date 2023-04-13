package com.xxl.job.admin.controller.category;

import com.xxl.job.admin.service.CategoryMgService;
import com.xxl.job.core.biz.model.MenuCategory;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 
 * @describe 编目管理
 *
 * @author zy
 *
 * @version 创建时间：2020年5月8日 下午3:38:19
 *
 */
@Controller
@RequestMapping("/categorymg")
public class CategoryMgController {

	@Autowired
	private CategoryMgService categoryMgService;
	
	/**
	 * @catalog 业务支撑平台/字典管理
	 * @title 新建字典分组
	 * @description  新建字典分组
	 * @method POST|GET
	 * @remark
	 */
	@GetMapping("/createcategory")
	@ResponseBody
	public ReturnT<String> createCategory(@RequestParam(value = "name") String name,
                                          @RequestParam(value = "type", defaultValue = "1") Integer type,
                                          @RequestParam(value = "parentId", defaultValue = "0") Long parentId) {

		return categoryMgService.createCategory(name, type, parentId);
	}


	/**
	 * showdoc
	 * @catalog 业务支撑平台/字典管理
	 * @title 更新字典分组
	 * @description 更新字典分组
	 * @method POST|GET
	 */
	@RequestMapping("/updatecategory")
	@ResponseBody
	public ReturnT<String> updateCategory(@RequestParam(value = "id") Long id,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "parentId", defaultValue = "0") Long parentId) {

		return categoryMgService.updateCategory(id, name, parentId);
	}

	/**
	 * showdoc
	 * @catalog 业务支撑平台/字典管理
	 * @title 字典分组查询
	 * @description 字典分组查询
	 * @method POST|GET
	 * @remark
	 */
	@GetMapping("/selectallcategorys")
	@ResponseBody
	public ReturnT<List<MenuCategory>> selectallcategorys(
			@RequestParam(value = "type", defaultValue = "1") Integer type) {
		return categoryMgService.selectallcategorys(type);
	}

	/**
	 * showdoc
	 * @catalog 业务支撑平台/字典管理
	 * @title 删除编目
	 * @description 删除编目
	 * @method POST|GET
	 * @remark
	 */
	@RequestMapping("/deletecategory")
	@ResponseBody
	public ReturnT<String> deletecategory(@RequestParam(value = "id") Long id) {

		return categoryMgService.deletecategory(id);
	}

}
