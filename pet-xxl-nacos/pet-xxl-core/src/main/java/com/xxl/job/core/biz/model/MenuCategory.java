package com.xxl.job.core.biz.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @describe 编目bean
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月8日 下午3:49:29
 *
 */
public class MenuCategory {

	
	public static final Integer DATADICTIONARY_TYPE = 1;
	
	public static final Integer ALGO_TYPE = 2;
	
	// 创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private Long id;

	// 子编目
	private MenuCategory menuCategory;

	// 名称
	private String name;

	// 父级id,没有父级，默认为0
	private Long parentId;

	private List<MenuCategory> sonMenuCategorys;

	// 排序id
	private Integer sortId;

	// 类型
	private Integer type;
	
	// 更新时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	public Date getCreateTime() {
		return createTime;
	}

	public Long getId() {
		return id;
	}

	public MenuCategory getMenuCategory() {
		return menuCategory;
	}

	public String getName() {
		return name;
	}

	public Long getParentId() {
		return parentId;
	}

	public List<MenuCategory> getSonMenuCategorys() {
		return sonMenuCategorys;
	}

	public Integer getSortId() {
		return sortId;
	}

	public Integer getType() {
		return type;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMenuCategory(MenuCategory menuCategory) {
		this.menuCategory = menuCategory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setSonMenuCategorys(List<MenuCategory> sonMenuCategorys) {
		this.sonMenuCategorys = sonMenuCategorys;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<Long> getids() {
		List<Long> ids = new ArrayList<Long>();
		setids(this, ids);
		return ids;
	}

	private void setids(MenuCategory menuCategory, List<Long> ids) {
		if (!ObjectUtils.isEmpty(menuCategory) && !ObjectUtils.isEmpty(menuCategory.getId())) {
			ids.add(menuCategory.getId());
			if (!CollectionUtils.isEmpty(menuCategory.getSonMenuCategorys())) {
				for (int i = 0; i < menuCategory.getSonMenuCategorys().size(); i++) {
					setids(menuCategory.getSonMenuCategorys().get(i), ids);
				}
			}
		}
	}

}
