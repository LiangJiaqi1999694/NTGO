package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.handler.IJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==IJobHandler.SUCCESS.getCode()?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }
        if(!ObjectUtils.isEmpty(handleCallbackParam.getExecuteResult())) {
            // handle msg
            StringBuffer handleMsg = new StringBuffer();
            if (log.getHandleMsg() != null) {
                handleMsg.append(log.getHandleMsg()).append("<br>");
            }
            if (handleCallbackParam.getExecuteResult().getMsg() != null) {
                handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
            }

            if (handleMsg.length() > 15000) {
                handleMsg = new StringBuffer(handleMsg.substring(0, 15000)); // text最大64kb 避免长度过长
            }

            // success, save log
            log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
            log.setHandleMsg(handleMsg.toString());

        }else {
            log.setHandleTime(new Date(handleCallbackParam.getLogDateTim()));
        }
        XxlJobCompleter.updateHandleInfoAndFinish(log);
        return ReturnT.SUCCESS;
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
    public ReturnT<String> registry(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
        if (ret < 1) {
            xxlJobRegistryDao.registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());

            // fresh
            freshGroupRegistryInfo(registryParam);
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        int ret = xxlJobRegistryDao.registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret > 0) {

            // fresh
            freshGroupRegistryInfo(registryParam);
        }
        return ReturnT.SUCCESS;
    }

    private void freshGroupRegistryInfo(RegistryParam registryParam){
        // Under consideration, prevent affecting core tables
    }

}
