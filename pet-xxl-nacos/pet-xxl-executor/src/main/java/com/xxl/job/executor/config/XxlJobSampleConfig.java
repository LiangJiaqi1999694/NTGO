package com.xxl.job.executor.config;

import com.xxl.job.executor.dao.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 
 * @describe 描述
 *
 * @author zhd
 *
 * @version 创建时间：2020年6月9日 下午2:46:21
 *
 */
@Component
public class XxlJobSampleConfig implements InitializingBean {

	private static XxlJobSampleConfig xxlJobSampleConfig = null;

	public static XxlJobSampleConfig getSampleConfig() {
		return xxlJobSampleConfig;
	}

	@Resource
	private AlgoMgDao algoMgDao;

	@Resource
	private AlgoParamDao algoParamDao;

	@Resource
	private JobHandleLogDao jobHandleLogDao;

	@Resource
	private JobParamDao jobParamDao;

	@Resource
	private XxlJobInfoDao xxlJobInfoDao;


	@Override
	public void afterPropertiesSet() throws Exception {
		xxlJobSampleConfig = this;
	}

	public AlgoMgDao getAlgoMgDao() {
		return algoMgDao;
	}

	public AlgoParamDao getAlgoParamDao() {
		return algoParamDao;
	}

	public JobHandleLogDao getJobHandleLogDao() {
		return jobHandleLogDao;
	}
	public JobParamDao getJobParamDao() {
		return jobParamDao;
	}

	public XxlJobInfoDao getXxlJobInfoDao() {
		return xxlJobInfoDao;
	}

}
