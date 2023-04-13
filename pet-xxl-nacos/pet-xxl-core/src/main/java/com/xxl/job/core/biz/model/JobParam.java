package com.xxl.job.core.biz.model;

/**
 * 
 * @describe job 参数
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月25日 下午2:18:52
 *
 */
public class JobParam {

	
	//枚举字典分组id
	private Long  dictionaryCategoryId;
	
	private Long id;
	
	
	// 0:不是重做参数 1:重做参数
	private Integer isredo = 1;
	
	// 0:不显示 1：显示
	private Integer isshow = 1;

	//任务id
	private Long jobId;

	//参数描述
	private String paramDescription;
	
	//参数id
	private Long paramId;
	
	//参数名称
	private String paramName;
	
	// 参数类型：1 string 2 date 3 枚举 默认1
	private Integer paramType = 1;
	
	//参数值
	private String paramValue;

	public JobParam() {
		super();
	}

	public Long getDictionaryCategoryId() {
		return dictionaryCategoryId;
	}

	public boolean isRedo() {
		return Integer.valueOf(1).equals(isredo);
	}
	
	public boolean isShow() {
		return Integer.valueOf(1).equals(isshow);
	}
	
	
	public Long getId() {
		return id;
	}

	public Integer getIsredo() {
		return isredo;
	}
	
	public Integer getIsshow() {
		return isshow;
	}


	public Long getJobId() {
		return jobId;
	}
	

	public String getParamDescription() {
		return paramDescription;
	}

	public Long getParamId() {
		return paramId;
	}

	public String getParamName() {
		return paramName;
	}
	
	public Integer getParamType() {
		return paramType;
	}

	public String getParamValue() {
		return paramValue;
	}


	public void setDictionaryCategoryId(Long dictionaryCategoryId) {
		this.dictionaryCategoryId = dictionaryCategoryId;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setIsredo(Integer isredo) {
		this.isredo = isredo;
	}

	public void setIsshow(Integer isshow) {
		this.isshow = isshow;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public void setParamDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}


	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	public void setParamType(Integer paramType) {
		this.paramType = paramType;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
