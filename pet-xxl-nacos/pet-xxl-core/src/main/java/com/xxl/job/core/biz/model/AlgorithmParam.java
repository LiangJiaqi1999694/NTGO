package com.xxl.job.core.biz.model;

/**
 * 
 * @describe 算法参数
 *
 * @author zhd
 *
 * @version 创建时间：2020年5月19日 下午2:31:24
 *
 */
public class AlgorithmParam {
	// 算法id
	private Long algoId;

	// 默认值
	private String defaultValue = "";
	
	//枚举字典分组id
	private Long  dictionaryCategoryId;

	//id
	private Long id;

	//0:不是重做参数 1：重做参数
	private Integer isRedo = 1;
	
	// 0:不显示 1：显示
	private Integer isShow = 1;
	
	// 参数描述
	private String paramDescription;
	// 参数名称
	private String paramName;

	// 参数类型：1 string 2 date 3 枚举 默认1
	private Integer paramType = 1;
	
	public AlgorithmParam() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getAlgoId() {
		return algoId;
	}

	public void setAlgoId(Long algoId) {
		this.algoId = algoId;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Long getDictionaryCategoryId() {
		return dictionaryCategoryId;
	}

	public void setDictionaryCategoryId(Long dictionaryCategoryId) {
		this.dictionaryCategoryId = dictionaryCategoryId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getIsRedo() {
		return isRedo;
	}

	public void setIsRedo(Integer isRedo) {
		this.isRedo = isRedo;
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public String getParamDescription() {
		return paramDescription;
	}

	public void setParamDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Integer getParamType() {
		return paramType;
	}

	public void setParamType(Integer paramType) {
		this.paramType = paramType;
	}
}
