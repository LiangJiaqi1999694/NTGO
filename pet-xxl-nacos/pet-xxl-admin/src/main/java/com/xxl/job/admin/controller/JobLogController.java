package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.JobHandleLogDao;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.util.RestTemplateClient;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Autowired
    private JobHandleLogDao jobHandleLogDao;
    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

        // 执行器列表
        List<XxlJobGroup> jobGroupList_all = xxlJobGroupDao.findAll();

        // filter group
        List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList == null || jobGroupList.size() == 0) {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }

        model.addAttribute("JobGroupList", jobGroupList);

        // 任务
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }

            model.addAttribute("jobInfo", jobInfo);

            // valid permission
            JobInfoController.validPermission(request, jobInfo.getJobGroup());
        }

        return "joblog/joblog.index";
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT<List<XxlJobInfo>>(list);
    }

    /**
     * @描述 日志列表
     * @author 庄勇
     * @date 9:35 2021/2/8 0008
     * @param
     * @return
     * @修改人和其他信息
     **/
    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(
            @RequestParam(required = false, defaultValue = "1") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            @RequestParam(required = false,defaultValue = "0") int jobGroup,
            int jobId,
            @RequestParam(required = false,defaultValue = "-1")int logStatus,
            String startTime, String endTime) {
        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (startTime != null && endTime != null) {
            triggerTimeStart = DateUtil.parseDateTime(startTime);
            triggerTimeEnd = DateUtil.parseDateTime(endTime);
        }


        if(start==0) {
            start = 1;
        }

        if(start>0) {
            start = start -1;
        }

        start = start*length;

        // page query
        List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd,
                logStatus);
        if(!CollectionUtils.isEmpty(list)) {
            Map<Integer,XxlJobInfo> jobs = new HashMap<>();
            for(int i=0;i<list.size();i++) {
                int localjobId = list.get(i).getJobId();
                if(localjobId>0) {
                    XxlJobInfo xxlJobInfo  = jobs.get(localjobId);
                    if(ObjectUtils.isEmpty(xxlJobInfo)) {
                        xxlJobInfo = xxlJobInfoDao.loadById(localjobId);
                        jobs.put(localjobId, xxlJobInfo);
                    }
                    list.get(i).setJobDesc(xxlJobInfo.getJobDesc());
                }
            }
        }

        int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd,
                logStatus);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count); // 总记录数
        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
        maps.put("data", list); // 分页列表
        return maps;
    }

    /**
     * @title 子任务日志列表
     */
    @RequestMapping("/pageSonTasksList")
    @ResponseBody
    public Map<String, Object> pageSonTasksList(HttpServletRequest request,
                                                @RequestParam(required = false, defaultValue = "1") int start,
                                                @RequestParam(required = false, defaultValue = "10") int length, int logId, @RequestParam(required = false,defaultValue ="-1")int logStatus) {


        if(start==0) {
            start = 1;
        }

        if(start>0) {
            start = start -1;
        }

        start = start*length;

        // page query
        List<JobHandleLog> list = jobHandleLogDao.pageList(start, length, logId, logStatus);
        Integer list_count = jobHandleLogDao.pageListCount(start, length, logId, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count); // 总记录数
        maps.put("recordsFiltered", list_count); // 过滤后的总记录数
        maps.put("data", list); // 分页列表
        return maps;
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(int id, Model model) {

        // base check
        ReturnT<String> logStatue = ReturnT.SUCCESS;
        XxlJobLog jobLog = xxlJobLogDao.load(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getId());
        return "joblog/joblog.detail";
    }

    // 执行日志
    @RequestMapping("/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(long logId, int fromLineNum) {
        try {
            XxlJobLog jobLog = xxlJobLogDao.load(logId);
            if(ObjectUtils.isEmpty(jobLog)) {
                return new ReturnT<LogResult>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_logid_unvalid"));
            }
//			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
//			ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));
            //TODO 重构nacos服务调用 by majun at 2020-05-15
            LogParam logParam = new LogParam(jobLog.getTriggerTime().getTime(), logId, fromLineNum);
            ReturnT<LogResult> logResult = RestTemplateClient.logDetailCat(jobLog.getExecutorAddress(), logParam);
            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/logKill")
    @ResponseBody
    public ReturnT<String> logKill(int id) {
        // base check
        XxlJobLog log = xxlJobLogDao.load(id);
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        // request of kill
        ReturnT<String> runResult = null;
        try {
//			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
//			runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
            //TODO 重构nacos服务调用 by majun at 2020-05-15
//			ExecutorBizApi executorBizApi = ApplicationContextHolder.getBean(ExecutorBizApi.class);
//            runResult = executorBizApi.kill(new KillParam(jobInfo.getId()));
            KillParam killParam = new KillParam(jobInfo.getId());
            String address = log.getExecutorAddress();
            runResult = RestTemplateClient.executorKill(address, killParam);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<String>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            log.setHandleCode(ReturnT.FAIL_CODE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(new Date());
            XxlJobCompleter.updateHandleInfoAndFinish(log);
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }

    @RequestMapping("/clearLog")
    @ResponseBody
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1); // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3); // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6); // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1); // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000; // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000; // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000; // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000; // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0; // 清理所有日志数据
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                xxlJobLogDao.clearLog(logIds);
                jobHandleLogDao.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }


    @RequestMapping("/deleteLog")
    @ResponseBody
    public ReturnT<String> clearLog(Long jobLogId) {
        if(xxlJobLogDao.deleteLog(jobLogId)>0) {
            jobHandleLogDao.deleteLog(jobLogId);
            return ReturnT.SUCCESS;
        }
        return ReturnT.FAIL;

    }

}
