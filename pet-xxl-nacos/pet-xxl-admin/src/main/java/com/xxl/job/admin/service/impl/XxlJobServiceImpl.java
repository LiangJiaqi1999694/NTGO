package com.xxl.job.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.*;
import com.xxl.job.admin.dao.algomg.AlgoMgDao;
import com.xxl.job.admin.dao.algomg.AlgoParamDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import com.xxl.job.core.util.QueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

/**
 * core job action for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
	private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;
	@Resource
	private XxlJobLogReportDao xxlJobLogReportDao;

	@Autowired
	AlgoMgDao algoMgDao;

	@Resource
	private JobHandleLogDao jobHandleLogDao;


	@Autowired
	AlgoParamDao algoParamDao;

	@Autowired
	JobParamDao jobParamDao;

	@Autowired
	RedisTemplate redisTemplate;

	// 需要新增参数标识
	private final String JOBPARAM_INSET_MARK = "INSERT";

	// 需要更新参数标识
	private final String JOBPARAM_UPDATE_MARK = "UPDATE";


	@Override
	public Map<String, Object> pageList(int start, int length, int triggerStatus, String jobDesc,Integer jobType,Long categoryId) {

		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, -1, triggerStatus, jobDesc,jobType,categoryId);
		int list_count = xxlJobInfoDao.pageListCount(start, length, -1, triggerStatus, jobDesc,jobType,categoryId);
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", list_count); // 总记录数
		maps.put("recordsFiltered", list_count); // 过滤后的总记录数
		maps.put("data", list); // 分页列表
		return maps;
	}

	@Override
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		// valid
		XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose")+I18nUtil.getString("jobinfo_field_jobgroup")) );
		}
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") );
		}
		if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_gluetype")+I18nUtil.getString("system_unvalid")) );
		}
		if (GlueTypeEnum.BEAN==GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorParam()==null || jobInfo.getExecutorHandler().trim().length()==0) ) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"JobHandler") );
		}

		// fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource()!=null) {
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
		}

		// ChildJobId valid
        if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}

		// add in db
		jobInfo.setAddTime(new Date());
		jobInfo.setUpdateTime(new Date());
		jobInfo.setGlueUpdatetime(new Date());
		xxlJobInfoDao.save(jobInfo);
		if (jobInfo.getId() < 1) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail")) );
		}

		return new ReturnT<String>(String.valueOf(jobInfo.getId()));
	}

	private boolean isNumeric(String str){
		try {
			int result = Integer.valueOf(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public ReturnT<String> update(XxlJobInfo jobInfo) {

		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") );
		}
		if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}

		// ChildJobId valid
        if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}

		// group valid
		XxlJobGroup jobGroup = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (jobGroup == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_jobgroup")+I18nUtil.getString("system_unvalid")) );
		}

		// stage job info
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
		if (exists_jobInfo == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id")+I18nUtil.getString("system_not_found")) );
		}

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
		if (exists_jobInfo.getTriggerStatus() == 1 && !jobInfo.getJobCron().equals(exists_jobInfo.getJobCron()) ) {
			try {
				Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
				if (nextValidTime == null) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
				}
				nextTriggerTime = nextValidTime.getTime();
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid")+" | "+ e.getMessage());
			}
		}

		exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
		exists_jobInfo.setJobCron(jobInfo.getJobCron());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
		exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
		exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
		exists_jobInfo.setTriggerNextTime(nextTriggerTime);

		exists_jobInfo.setUpdateTime(new Date());
        xxlJobInfoDao.update(exists_jobInfo);


		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> remove(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
		if (xxlJobInfo == null) {
			return ReturnT.SUCCESS;
		}

		xxlJobInfoDao.delete(id);
		xxlJobLogDao.delete(id);
		xxlJobLogGlueDao.deleteByJobId(id);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> start(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = 0;
		try {
			Date nextValidTime = new CronExpression(xxlJobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
			if (nextValidTime == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
			}
			nextTriggerTime = nextValidTime.getTime();
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid")+" | "+ e.getMessage());
		}

		xxlJobInfo.setTriggerStatus(1);
		xxlJobInfo.setTriggerLastTime(System.currentTimeMillis());
		xxlJobInfo.setTriggerNextTime(nextTriggerTime);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> stop(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		xxlJobInfo.setTriggerStatus(0);
		xxlJobInfo.setTriggerLastTime(System.currentTimeMillis());
		xxlJobInfo.setTriggerNextTime(0);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = xxlJobInfoDao.findAllCount();
		int jobLogCount = 0;
		int jobLogSuccessCount = 0;
		int jobRunningCount = 0;
		int jobFailCount = 0;
		XxlJobLogReport xxlJobLogReport = xxlJobLogReportDao.queryLogReportTotal();
		if (xxlJobLogReport != null) {
			jobLogCount = xxlJobLogReport.getRunningCount() + xxlJobLogReport.getSucCount()
					+ xxlJobLogReport.getFailCount();
			jobLogSuccessCount = xxlJobLogReport.getSucCount();
			jobRunningCount = xxlJobLogReport.getRunningCount();
			jobFailCount = xxlJobLogReport.getFailCount();
		}

		// executor count
		Set<String> executorAddressSet = new HashSet<String>();
		List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();

		if (groupList != null && !groupList.isEmpty()) {
			for (XxlJobGroup group : groupList) {
				if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
					executorAddressSet.addAll(group.getRegistryList());
				}
			}
		}

		int executorCount = executorAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		// 任务数量
		dashboardMap.put("jobInfoCount", jobInfoCount);
		// 调度次数
		dashboardMap.put("jobLogCount", jobLogCount);
		//成功数量
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		// 执行器数量
		dashboardMap.put("executorCount", executorCount);
		// 调度进行中数量
		dashboardMap.put("jobRunningCount", jobRunningCount);
		// 调度失败数量
		dashboardMap.put("jobFailCount", jobFailCount);
		// 调度失败数量
		dashboardMap.put("jobFailRate", new DecimalFormat("0.00%").format(jobFailCount/(jobLogCount*1.0)));
		// 调度进行中比例
		dashboardMap.put("jobRunningRate", new DecimalFormat("0.00%").format(jobRunningCount/(jobLogCount*1.0)));
		// 调度成功比例
		dashboardMap.put("jobSuccessRate", new DecimalFormat("0.00%").format(jobLogSuccessCount/(jobLogCount*1.0)));
		return dashboardMap;
	}

	@Override
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {

		// process
		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountRunningTotal = 0;
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<XxlJobLogReport> logReportList = xxlJobLogReportDao.queryLogReport(startDate, endDate);

		if (logReportList!=null && logReportList.size()>0) {
			for (XxlJobLogReport item: logReportList) {
				String day = DateUtil.formatDate(item.getTriggerDay());
				int triggerDayCountRunning = item.getRunningCount();
				int triggerDayCountSuc = item.getSucCount();
				int triggerDayCountFail = item.getFailCount();

				triggerDayList.add(day);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);

				triggerCountRunningTotal += triggerDayCountRunning;
				triggerCountSucTotal += triggerDayCountSuc;
				triggerCountFailTotal += triggerDayCountFail;
			}
		} else {
			for (int i = -6; i <= 0; i++) {
				triggerDayList.add(DateUtil.formatDate(DateUtil.addDays(new Date(), i)));
				triggerDayCountRunningList.add(0);
				triggerDayCountSucList.add(0);
				triggerDayCountFailList.add(0);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerDayList", triggerDayList);
		result.put("triggerDayCountRunningList", triggerDayCountRunningList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);

		result.put("triggerCountRunningTotal", triggerCountRunningTotal);
		result.put("triggerCountSucTotal", triggerCountSucTotal);
		result.put("triggerCountFailTotal", triggerCountFailTotal);

		return new ReturnT<Map<String, Object>>(result);
	}


	@Override
	public ReturnT<Map<String, Object>> timeTask() {
		Object timeTask = redisTemplate.opsForValue().get("timeTaskScheduled");
		if(timeTask==null){
			return new ReturnT<>(this.queryTask());
		}
		Object map = JSON.parse((String)timeTask);

		return new ReturnT<>((Map<String, Object>)map);
	}

	@Override
	public void timeTaskScheduled() {
		redisTemplate.opsForValue().set("timeTaskScheduled",JSON.toJSONString(this.queryTask()));
	}


	@Override
	public ReturnT<XxlJobInfo> selectbyId(int id, long algoid) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
		if (ObjectUtils.isEmpty(xxlJobInfo) && algoid <= 0) {
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, "Id " + id + "不存在");
		}

		if (ObjectUtils.isEmpty(xxlJobInfo)) {
			xxlJobInfo = new XxlJobInfo();
		}

		if (ObjectUtils.isEmpty(xxlJobInfo.getAlgoId())) {
			xxlJobInfo.setAlgoId(algoid);
		}

		if (!ObjectUtils.isEmpty(xxlJobInfo.getAlgoId())) {
			Algorithm algorithm = algoMgDao.selectAlgosById(xxlJobInfo.getAlgoId());
			xxlJobInfo.setJobhandler(algorithm.getJobhandler());
			xxlJobInfo.setAlgoName(algorithm.getName());
			List<AlgorithmParam> algorithmParams = algoParamDao.selectParamsByAlgoId(xxlJobInfo.getAlgoId());
			List<JobParam> jobParams = jobParamDao.selectByJobId(id);
			setJobParams(xxlJobInfo, algorithmParams, jobParams);
		}
		return new ReturnT<XxlJobInfo>(xxlJobInfo);
	}
	/**
	 * 加载任务信息
	 *
	 * @param xxlJobInfo
	 * @param algorithmParams 算法参数
	 * @param jobParams       任务参数
	 */
	public void setJobParams(XxlJobInfo xxlJobInfo, List<AlgorithmParam> algorithmParams, List<JobParam> jobParams) {
		if (!CollectionUtils.isEmpty(algorithmParams)) {
			List<JobParam> params = new ArrayList<>();
			JobParam jobParam = null;
			boolean isExist = false;
			for (int i = 0; i < algorithmParams.size(); i++) {
				isExist = false;
				if (!CollectionUtils.isEmpty(jobParams)) {
					for (int j = 0; j < jobParams.size(); j++) {
						if (algorithmParams.get(i).getId().equals(jobParams.get(j).getParamId())) {
							jobParam = jobParams.get(j);
							isExist = true;
							break;
						}
					}
				}
				if (!isExist) {
					jobParam = new JobParam();
					jobParam.setParamValue(algorithmParams.get(i).getDefaultValue());
				}
				jobParam.setParamId(algorithmParams.get(i).getId());
				jobParam.setParamType(algorithmParams.get(i).getParamType());
				jobParam.setParamName(algorithmParams.get(i).getParamName());
				jobParam.setParamDescription(algorithmParams.get(i).getParamDescription());
				jobParam.setDictionaryCategoryId(algorithmParams.get(i).getDictionaryCategoryId());
				jobParam.setIsshow(algorithmParams.get(i).getIsShow());
				jobParam.setIsredo(algorithmParams.get(i).getIsRedo());
				params.add(jobParam);
			}
			xxlJobInfo.setJobParams(params);
		}
	}

	public Map<String, Object> queryTask() {
		List<Map<String,Object>> jobHandleLogs = jobHandleLogDao.groupByTimeHandle();
		Map<String, Object> objectMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(jobHandleLogs)){
			String[] namesX = new String[jobHandleLogs.size()];
			String[] handles = new String[jobHandleLogs.size()];
			int[] timeY = new int[jobHandleLogs.size()];
			for(int i=0;i<jobHandleLogs.size();i++){
				Long algoId = Long.valueOf(jobHandleLogs.get(i).get("algoId").toString());
				Algorithm algorithm = algoMgDao.selectAlgosById(algoId);
				namesX[i] = algorithm.getName();
				handles[i] = algorithm.getJobhandler();
				Long timeTotle = Long.valueOf(jobHandleLogs.get(i).get("timeTotle").toString());
				int taskCount = Integer.valueOf(jobHandleLogs.get(i).get("taskCount").toString());
				//每个任务每分钟耗时
				timeY[i] = (int)Math.ceil(timeTotle/(taskCount*1000));
			}
			objectMap.put("names",namesX);
			objectMap.put("times",timeY);
			objectMap.put("handles",handles);
		}
		return objectMap;
	}

	@Override
	public ReturnT<PageInfo> failTaskDetail(String executorAddress, int jobGroup, int pageSize, int pageNum){

		PageHelper.startPage(pageNum, pageSize);
		List<Map<String,Object>> findFailJobs = xxlJobLogDao.findFailJobs(executorAddress,jobGroup);
		PageInfo pageInfo = new PageInfo(findFailJobs);
		return new ReturnT<>(pageInfo);
	}

	@Override
	public List<Map<String,Object>> queueTrigger(){
		List<Map<String,Object>> list = new ArrayList<>();

		List<XxlJobRegistry> xxlJobRegistries = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());

		xxlJobRegistries.stream().forEach(xxl->{
			String address = xxl.getRegistryValue();
			try {
				URL url = new URL(address);
				String node = url.getHost()+":" + url.getPort();
				Object queues = redisTemplate.opsForValue().get("queue:"+ node);
				if(queues!=null){
					List<Map<String,Object>> dataList = new ArrayList<>();
					Map<String,Object> map = new HashMap<>();
					List<QueueInfo>  queueInfos = JSON.parseArray((String)queues,QueueInfo.class);

					Integer totle = 0;
					for(QueueInfo q: queueInfos){
						Map<String,Object> dataMap = new HashMap<>();
						Long jobId = Long.valueOf(q.getJobId());
						XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(jobId.intValue());
						dataMap.put("jobDesc",xxlJobInfo.getJobDesc());
						Integer queueSize = q.getQueueSize();
						dataMap.put("len",queueSize);
						dataMap.put("appname",xxl.getRegistryKey());
						totle = totle + queueSize;
						dataList.add(dataMap);
					}
					map.put("node",node);
					map.put("totle",totle);
					map.put("data",dataList);
					list.add(map);
				}
			} catch (MalformedURLException e) {
			}
		});

		return list;
	}


	@Override
	public ReturnT<List<Map<String, Object>>> failTask() {
		//查找所有任务失败节点
		List<Map<String,Object>> mapList = xxlJobLogDao.findFailJob();

		List<Map<String,Object>> list = new ArrayList<>();
		if(!CollectionUtils.isEmpty(mapList)){
			mapList.stream().forEach(m->{
				Map<String,Object> map = new HashMap<>();
				String node = m.get("executorAddress").toString();
				//执行器注册的进行显示没有注册的 不显示
				map = m;
				try {
					URL url = new URL(node);
					map.put("nodeIp",url.getHost()+":" + url.getPort() );
					list.add(map);
				} catch (MalformedURLException e) {
				}
			});
		}
		return new ReturnT<>(list);
	}

	@Override
	@Transactional
	public ReturnT<String> save(XxlJobInfo jobInfo) {
		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron().trim())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE,
					(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE,
					(I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
		}
		// add in db
		Date now = new Date();
		jobInfo.setUpdateTime(now);
		if (ObjectUtils.isEmpty(jobInfo.getId())||jobInfo.getId()==0) {// 新增
			if(ObjectUtils.isEmpty(jobInfo.getJobGroup())) {
				jobInfo.setJobGroup(0);
			}
			jobInfo.setAddTime(now);
			if (xxlJobInfoDao.save(jobInfo) < 1) {
				return new ReturnT<String>(ReturnT.FAIL_CODE,
						(I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail")));
			}
		} else {// 保存
			boolean isAlgoChange = false;
			XxlJobInfo oldXxlJobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
			if(!ObjectUtils.isEmpty(oldXxlJobInfo.getAlgoId())&&!ObjectUtils.isEmpty(jobInfo.getAlgoId())) {
				if(!oldXxlJobInfo.getAlgoId().equals(jobInfo.getAlgoId())) {
					isAlgoChange = true;
				}
			}
			if (xxlJobInfoDao.update(jobInfo) < 1) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "保存失败！");
			}else {
				//删除旧任务参数
				if(isAlgoChange) {
					jobParamDao.deleteByJobId(oldXxlJobInfo.getAlgoId());
				}
			}
		}

		// 保存任务参数
		if (!ObjectUtils.isEmpty(jobInfo.getAlgoId())) {
			List<JobParam> params = jobInfo.getJobParams();
			if (!CollectionUtils.isEmpty(params)) {
				for (int i = 0; i < params.size(); i++) {
					Integer jobInfoId = jobInfo.getId();
					params.get(i).setJobId(jobInfoId.longValue());
				}
			}

			List<AlgorithmParam> algorithmParams = algoParamDao.selectParamsByAlgoId(jobInfo.getAlgoId());
			Map<String, List<JobParam>> needInsertJobParams = getNeedInsertJobParams(algorithmParams, jobInfo);
			// 任务参数保存
			List<JobParam> insertJobParams = needInsertJobParams.get(JOBPARAM_INSET_MARK);
			if (!CollectionUtils.isEmpty(insertJobParams)) {
				jobParamDao.batchInsertParams(insertJobParams);
			}

			// 更新参数
			List<JobParam> updateJobParams = needInsertJobParams.get(JOBPARAM_UPDATE_MARK);
			if (!CollectionUtils.isEmpty(updateJobParams)) {
				for (int i = 0; i < updateJobParams.size(); i++) {
					jobParamDao.updateParams(updateJobParams.get(i));
				}
			}
		}

		return ReturnT.ok("保存成功！");
	}
	/**
	 *
	 * @param algorithmParams
	 * @param xxlJobInfo
	 * @return
	 */
	private Map<String, List<JobParam>> getNeedInsertJobParams(List<AlgorithmParam> algorithmParams,
															   XxlJobInfo xxlJobInfo) {
		Map<String, List<JobParam>> maps = new HashMap<String, List<JobParam>>();
		List<JobParam> jobParams = xxlJobInfo.getJobParams();

		if (!CollectionUtils.isEmpty(jobParams)) {
			List<JobParam> insertJobParams = new ArrayList<JobParam>();
			List<JobParam> updateJobParams = new ArrayList<JobParam>();
			JobParam jobParam = null;
			for (int i = 0; i < algorithmParams.size(); i++) {
				boolean isExist = false;
				if (!CollectionUtils.isEmpty(algorithmParams)) {
					for (int j = 0; j < jobParams.size(); j++) {
						if (algorithmParams.get(i).getId().equals(jobParams.get(j).getParamId())) {
							jobParam = jobParams.get(j);
							if(!ObjectUtils.isEmpty(jobParams.get(j).getId())) {
								isExist = true;
							}
							break;
						}
					}
				}

//				if (!isExist) {
//					jobParam.setId(uidGenerator.getUID());
//				}
				Integer id = xxlJobInfo.getId();
				jobParam.setJobId(id.longValue());
				jobParam.setParamId(algorithmParams.get(i).getId());
				jobParam.setParamType(algorithmParams.get(i).getParamType());
				jobParam.setParamName(algorithmParams.get(i).getParamName());
				jobParam.setParamDescription(algorithmParams.get(i).getParamDescription());
				jobParam.setDictionaryCategoryId(algorithmParams.get(i).getDictionaryCategoryId());
				jobParam.setIsshow(algorithmParams.get(i).getIsShow());
				jobParam.setIsredo(algorithmParams.get(i).getIsRedo());

				if (!isExist) {
					insertJobParams.add(jobParam);
				}else {
					updateJobParams.add(jobParam);
				}

			}
			maps.put(JOBPARAM_INSET_MARK, insertJobParams);
			maps.put(JOBPARAM_UPDATE_MARK, updateJobParams);

		}
		return maps;
	}

}
