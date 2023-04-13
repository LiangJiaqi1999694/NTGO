package com.xxl.job.core.biz.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;

import java.util.Date;
import java.util.List;

/**
 * 
 * @describe 原子算法
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月19日 下午2:24:53
 *
 */
public class Algorithm {

	/**阻塞处理策略：0：单机串行1：单机并行*/
	private Integer blockStrategy;
	
	/** 分组id */
	private Long categoryId;

	/** 创建时间 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createtime;

	/** 算法描述 */
	private String description;
	
	private Long id;
	
	/** 处理的jobhandler */
	private String jobhandler;
	
	/** 算法名称 */
	private String name;

	/**输入参数*/
	private List<AlgorithmParam> params;

	/** 集群id */
	private Integer registryId;

	/**状态值0 禁用 1 启用*/
	private Integer state = 1;
	
	/** 更新时间 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updatetime;

	public Integer getBlockStrategy() {
		return blockStrategy;
	}
	
	public String getBlockStrategyName() {
		if(Integer.valueOf(0).equals(blockStrategy)) {
			return ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name();
		}else if(Integer.valueOf(1).equals(blockStrategy)) {
			return ExecutorBlockStrategyEnum.CONCURRENT_EXECUTION.name();
		}
		return null;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public String getJobhandler() {
		return jobhandler;
	}


	public String getName() {
		return name;
	}


	public List<AlgorithmParam> getParams() {
		return params;
	}

	public Integer getRegistryId() {
		return registryId;
	}

	public Integer getState() {
		return state;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setBlockStrategy(Integer blockStrategy) {
		this.blockStrategy = blockStrategy;
	}
	

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setJobhandler(String jobhandler) {
		this.jobhandler = jobhandler;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParams(List<AlgorithmParam> params) {
		this.params = params;
	}

	public void setRegistryid(Integer registryId) {
		this.registryId = registryId;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

}
