package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ChildJobRedoVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@RestController
@RequestMapping("/jobinfo")
public class JobInfoController {

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobService xxlJobService;


	/**
	 * showdoc
	 *
	 * @catalog 业务支撑平台/任务管理/
	 * @title 新增任务
	 * @description 新增任务
	 * @method POST|GET
	 * @url http://192.168.11.92:9090/xxl-job-admin/jobinfo/save
	 * @param id Long ID
	 * @param alarmEmail string 告警邮箱
	 * @param executorFailRetryCount int 失败重试次数
	 * @param algoId int 算法id
	 * @param productId Long 产品id
	 * @param executorRouteStrategy string 路由策略：FIRST：第一个，LAST：最后一个，ROUND：轮询，RANDOM：随机，CONSISTENT_HASH：一致性HASH，LEAST_FREQUENTLY_USED：最不经常使用，LEAST_RECENTLY_USED：最近最久未使用，FAILOVER：故障转移，BUSYOVER：忙碌转移，SHARDING_BROADCAST：分片广播
	 * @param executorTimeout int 任务超时时间，单位秒
	 * @param jobType int 任务类型：1、数据汇集2、数据预处理3、数据归档4、产品生产5、其他
	 * @param jobCron string 执行策略
	 * @param jobDesc string 任务描述
	 * @return {"code":200,"msg":null,"content":"新增成功!"}
	 * @return_param code int 状态码200:成功500：异常
	 * @remark JSON对象格式{"alarmEmail":"642991278@qq.com","algoId":2165121257621741568,"executorBlockStrategy":"SERIAL_EXECUTION","executorFailRetryCount":2,"executorRouteStrategy":"FIRST","executorTimeout":3600,"glueType":"BEAN","jobCron":"0 0,1 * * * ? ","jobDesc":"测试1","jobGroup":0,"jobParams":[{"isredo":1,"isshow":1,"paramId":2165121257621741569,"paramType":1,"paramValue":"ccc"}],"productId":2166584140662767616,"shardingRouteStrategy":false,"triggerLastTime":0,"triggerNextTime":0,"triggerStatus":0}
	 */
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(@RequestBody XxlJobInfo jobInfo) {
		return xxlJobService.save(jobInfo);
	}

    /**
     * showdoc
     * @catalog 业务支撑平台/任务管理/
     * @title 子任务重做
     * @description  子任务重做
     * @method POST|GET
     * @url http://192.168.11.92:9090/xxl-job-admin/jobinfo/redo
     * @param handleLogId	 必选 long 产品id
     * @param paramId	 必选 long 插件参数id
     * @param paramValue    必选  String 参数值
     * @return {"code":200,"msg":"提交重做任务成功！","content":null}
     * @remark JSONOBJECT {"handleLogId":2257172834969296896,"jobParams":[{"isredo":1,"isshow":1,"paramId":2175641487956918273,"paramType":1,"paramValue":"234"}]}
     */
    @ResponseBody
    @PostMapping("/redo")
    public ReturnT<String> redo(@RequestBody ChildJobRedoVo jobRedoVo){
        return xxlJobService.redo(jobRedoVo);
    }

    /**
	 * showdoc
	 *
	 * @catalog 业务支撑平台/任务管理/
	 * @title 根据任务id查询任务详情
	 * @description 根据任务id查询任务详情
	 * @method POST|GET
	 * @url http://192.168.11.92:9090/xxl-job-admin/jobinfo/selectbyid
	 * @param id         查询必选 int 任务id
	 * @param algoid     绑定算法必选 int 算法id
	 * @return {"code":200,"msg":null,"content":"更新成功!"}
	 * @return_param addTime string 创建时间
	 * @return_param alarmEmail string 告警邮箱
	 * @return_param author string 负责人
	 * @return_param executorBlockStrategy string 阻塞策略：SERIAL_EXECUTION：单机串行，CONCURRENT_EXECUTION：单机并行
	 * @return_param executorFailRetryCount int 失败重试次数
	 * @return_param executorHandler string jobhander
	 * @return_param executorRouteStrategy string 路由策略：FIRST：第一个，LAST：最后一个，ROUND：轮询，RANDOM：随机，CONSISTENT_HASH：一致性HASH，LEAST_FREQUENTLY_USED：最不经常使用，LEAST_RECENTLY_USED：最近最久未使用，FAILOVER：故障转移，BUSYOVER：忙碌转移，SHARDING_BROADCAST：分片广播
	 * @return_param executorTimeout int 任务超时时间
	 * @return_param jobCron string 执行策略
	 * @return_param jobDesc string 任务描述
	 * @return_param jobType Integer 任务类型
	 * @return_param productId Long 产品id
	 * @return_param jobGroup int 执行器组id
	 * @return_param triggerLastTime string 最后触发时间
	 * @return_param triggerNextTime string 下次触发时间
	 * @return_param triggerStatus int 调度状态：-1-全部，0-停止，1-运行
	 * @return_param updateTime string 更新时间
	 * @return_param dictionaryCategoryId Integer 参数枚举字典分组id
	 * @return_param isredo Integer 0:不是重做参数 1:重做参数
	 * @return_param isshow Integer 0:不显示 1：显示
	 * @return_param jobId Long 参数所属任务id
	 * @return_param paramDescription String 参数描述
	 * @return_param paramName String 参数描述
	 * @return_param paramType Integer 参数类型
	 * @return_param paramValue String 参数值
	 * @remark
	 */
	@RequestMapping("/selectbyid")
	@ResponseBody
	public ReturnT<XxlJobInfo> selectbyId(@RequestParam(defaultValue = "-1",required = false)int id, @RequestParam(defaultValue = "-1",required = false)long algoid) {
		return xxlJobService.selectbyId(id,algoid);
	}

    @RequestMapping("/batchstop")
    @ResponseBody
    public ReturnT<String> batchpause(@RequestBody int[] ids) {
        if(ids!=null&&ids.length>0) {
            return xxlJobService.batchstop(ids);
        }
        return ReturnT.FAIL;
    }

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典

		// 执行器列表
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList==null || jobGroupList.size()==0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all){
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupList_all!=null && jobGroupList_all.size()>0) {
			XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
			if (loginUser.getRole() == 1) {
				jobGroupList = jobGroupList_all;
			} else {
				List<String> groupIdStrs = new ArrayList<>();
				if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
					groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
				}
				for (XxlJobGroup groupItem:jobGroupList_all) {
					if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
						jobGroupList.add(groupItem);
					}
				}
			}
		}
		return jobGroupList;
	}
	public static void validPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
		}
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "1") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										@RequestParam(required = false, defaultValue = "-1") int triggerStatus,
										@RequestParam(required = false)String jobDesc,@RequestParam(required = false)Integer jobType,@RequestParam(required = false)Long categoryId) {

		if(start==0) {
			start = 1;
		}

		if(start>0) {
			start = start -1;
		}

		start = start*length;
		return xxlJobService.pageList(start, length, triggerStatus, jobDesc,jobType,categoryId);
	}

    /**
     *
     * @catalog 业务支撑平台/任务管理/
     * @title 批量启动任务
     * @description 批量启动任务
     * @return {"code":200,"msg":null,"content":"更新成功!"}
     * @return_param code int 状态码200:成功500：异常
     * @remark
     */
    @RequestMapping("/batchstart")
    @ResponseBody
    public ReturnT<String> batchstart(@RequestBody int[] ids) {
        if(ids!=null&&ids.length>0) {
            return xxlJobService.batchstart(ids);
        }
        return ReturnT.FAIL;
    }

	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}

	@RequestMapping("/stop")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}

	@RequestMapping("/start")
	@ResponseBody
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}

	@RequestMapping("/trigger")
	@ResponseBody
	//@PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}
		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(String cron) {
		List<String> result = new ArrayList<>();
		try {
			CronExpression cronExpression = new CronExpression(cron);
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = cronExpression.getNextValidTimeAfter(lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (ParseException e) {
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		return new ReturnT<List<String>>(result);
	}

}
