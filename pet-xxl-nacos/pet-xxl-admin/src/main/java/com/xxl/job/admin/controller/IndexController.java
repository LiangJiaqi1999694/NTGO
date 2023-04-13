package com.xxl.job.admin.controller;

import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.SneakyThrows;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	@Resource
	private XxlJobService xxlJobService;
	@Resource
	private LoginService loginService;


	@RequestMapping("/dashboardInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> dashboardInfo() {
		Map<String, Object> dashboardInfo = xxlJobService.dashboardInfo();
		return new ReturnT<>(dashboardInfo);

	}

	/**
	 * @catalog 业务支撑平台/系统监控
	 * @title 任务队列
	 */
	@RequestMapping("/queueTrigger")
	@ResponseBody
	public ReturnT<List<Map<String,Object>> > queueTrigger() {
		return new ReturnT(xxlJobService.queueTrigger());
	}


	/**
	 *
	 * @catalog 业务支撑平台/系统监控
	 * @title 耗时任务统计
	 * @description 耗时任务统计
	 * @remark
	 */
	@RequestMapping("/timeTask")
	@ResponseBody
	public ReturnT<Map<String, Object>> timeTask() {
		ReturnT<Map<String, Object>> timeTask = xxlJobService.timeTask();
		return timeTask;

	}

	@Scheduled(cron = "0 0/1 * * * ?")
	public void timeTaskScheduled() {
		xxlJobService.timeTaskScheduled();
	}

	@RequestMapping("/")
	public String index(Model model) {

		Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
		model.addAllAttributes(dashboardMap);

		return "index";
	}

	/**
	 *
	 * @catalog 业务支撑平台/系统监控
	 * @title 运行报表详情：时间区间筛选，左侧折线图 + 右侧饼图
	 * @remark
	 */
	@SneakyThrows
	@RequestMapping("/chartInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> chartInfo(String startDate, String endDate) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date start = simpleDateFormat.parse(startDate);
		Date end = simpleDateFormat.parse(endDate);

		ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(start, end);
		return chartInfo;
	}

	/**
	 *
	 *
	 * @title 耗时任务统计详情
	 * @description 耗时任务统计详情
	 */
	@RequestMapping("/failTask")
	@ResponseBody
	public ReturnT<List<Map<String, Object>>> failTask() {
		ReturnT<List<Map<String, Object>>> failTask = xxlJobService.failTask();
		return failTask;
	}

	/**
	 * @title 失败任务详情
	 * @method GET
	 */
	@RequestMapping("/failTaskDetail")
	@ResponseBody
	public ReturnT<PageInfo> failTaskDetail(
			@RequestParam(required = false, defaultValue = "1") int pageNum,
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam String executorAddress,@RequestParam int jobGroup) {
		ReturnT<PageInfo> failTask = xxlJobService.failTaskDetail(executorAddress,jobGroup,pageSize,pageNum);
		return failTask;
	}

//    @RequestMapping("/chartInfo")
//	@ResponseBody
//	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
//        ReturnT<Map<String, Object>> chartInfo = xxlJobService.chartInfo(startDate, endDate);
//        return chartInfo;
//    }
	
	@RequestMapping("/toLogin")
	@PermissionLimit(limit=false)
	public String toLogin(HttpServletRequest request, HttpServletResponse response) {
		if (loginService.ifLogin(request, response) != null) {
			return "redirect:/";
		}
		return "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
		boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
		return loginService.login(request, response, userName, password, ifRem);
	}
	
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		return loginService.logout(request, response);
	}
	
	@RequestMapping("/help")
	public String help() {

		/*if (!PermissionInterceptor.ifLogin(request)) {
			return "redirect:/toLogin";
		}*/

		return "help";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
