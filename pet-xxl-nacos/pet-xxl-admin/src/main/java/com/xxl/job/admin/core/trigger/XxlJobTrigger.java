package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.util.RestTemplateClient;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class XxlJobTrigger {
    private static Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     * @param addressList           null: use executor addressList
     *                              not null: cover
     */
    public static void trigger(int jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorShardingParam,
                                Long handleLogId,
                               List<JobParam> jobParams,
                               String addressList,boolean redo) {

        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        Algorithm algorithm  = null;
        if(ObjectUtils.isEmpty(jobInfo.getAlgoId())||ObjectUtils.isEmpty(algorithm = XxlJobAdminConfig.getAdminConfig().getAlgoMgDao().selectAlgosById(jobInfo.getAlgoId()))) {
            logger.warn(">>>>>>>>>>>> trigger fail, executorHandler invalid，jobId={}", jobId);
            return;
        }
        // 最后一次执行时间
        jobInfo.setTriggerLastTime(System.currentTimeMillis());
        XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().update(jobInfo);

        //set jobhandler
        jobInfo.setJobhandler(algorithm.getJobhandler());
        //set executorBlockStrategy
        jobInfo.setExecutorBlockStrategy(algorithm.getBlockStrategyName());
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();

        Integer registryId = algorithm.getRegistryId();
        //set jobGroup
        jobInfo.setJobGroup(registryId);
        XxlJobGroup group = null;
        if(!ObjectUtils.isEmpty(registryId)) {
            group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(algorithm.getRegistryId());
        }

        if(!ObjectUtils.isEmpty(group)) {
            // cover addressList
            if (addressList != null && addressList.trim().length() > 0) {
                group.setAddressType(1);
                group.setAddressList(addressList.trim());
            }

            // sharding param
            int[] shardingParam = null;
            if (executorShardingParam != null) {
                String[] shardingArr = executorShardingParam.split("/");
                if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                    shardingParam = new int[2];
                    shardingParam[0] = Integer.valueOf(shardingArr[0]);
                    shardingParam[1] = Integer.valueOf(shardingArr[1]);
                }
            }
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum
                    .match(jobInfo.getExecutorRouteStrategy(), null) && !CollectionUtils.isEmpty(group.getRegistryList())&& shardingParam == null) {
                for (int i = 0; i < group.getRegistryList().size(); i++) {
                    processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size(),handleLogId,jobParams,redo);
                }
            } else {
                if (shardingParam == null) {
                    shardingParam = new int[] { 0, 1 };
                }
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1],handleLogId,jobParams,redo);
            }
        }else {
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType,0,1,handleLogId,jobParams,redo);
        }

    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount,
                                       TriggerTypeEnum triggerType, int index, int total, Long handleLogId, List<JobParam> jobParams, boolean redo) {

        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(),
                ExecutorBlockStrategyEnum.SERIAL_EXECUTION); // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum
                .match(jobInfo.getExecutorRouteStrategy(), null); // route strategy
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum)
                ? String.valueOf(index).concat("/").concat(String.valueOf(total))
                : null;

        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setRedo(redo);
        triggerParam.setRedoParams(jobParams);
        triggerParam.setRedoHandleLogId(handleLogId);
        triggerParam.setExecutorHandler(jobInfo.getJobhandler());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (!ObjectUtils.isEmpty(group)&&!CollectionUtils.isEmpty(group.getRegistryList())) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<String>(ReturnT.FAIL_CODE,
                    I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<String>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：")
                .append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                .append(ObjectUtils.isEmpty(group)?I18nUtil.getString("jobgroup_field_addressType_2"):(group.getAddressType() == 0) ? I18nUtil.getString("jobgroup_field_addressType_0")
                        : I18nUtil.getString("jobgroup_field_addressType_1"));
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：")
                .append(ObjectUtils.isEmpty(group)?"空":group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：")
                .append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：")
                .append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：")
                .append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：")
                .append(finalFailRetryCount);

        triggerMsgSb
                .append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"
                        + I18nUtil.getString("jobconf_trigger_run") + "<<<<<<<<<<< </span><br>")
                .append((routeAddressResult != null && routeAddressResult.getMsg() != null)
                        ? routeAddressResult.getMsg() + "<br><br>"
                        : "")
                .append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getJobhandler());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        // jobLog.setTriggerTime();
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);

        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        try {
//            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
//            runResult = executorBiz.run(triggerParam);
            //TODO 重构通过naocs服务调用到执行器处理执行任务 by majun at 2020-05-14
            runResult = RestTemplateClient.runExecutor(address, triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        return runResult;
    }

}
