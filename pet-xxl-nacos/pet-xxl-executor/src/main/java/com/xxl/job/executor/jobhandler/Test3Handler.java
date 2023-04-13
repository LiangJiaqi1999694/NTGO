package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试handler
 * @describe 描述 
 *
 * @author zhd
 *
 * @version 创建时间：2020年6月16日 下午3:56:46
 *
 */
@Component
public class Test3Handler extends BaseJobHandler<String>{
	
	
	@Override
	public List<String> getChildJobs(XxlJobInfo xxlJobInfo) {
		List<String> ls = new ArrayList<String>();
		for(int i=0;i<2;i++) {
			ls.add("测试："+i);
		}
		return ls;
	}

	@Override
	public ReturnT<String> processChildJobs(XxlJobInfo xxlJobInfo, String t) {
		
		 XxlJobLogger.log("处理："+t);
		 String test = xxlJobInfo.getParamValue("test");
		 XxlJobLogger.log("test："+test);
		 try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ReturnT.SUCCESS;
	}
	
	
}

