package com.pet.resource.api;

import com.pet.common.core.exception.ServiceException;
import com.pet.resource.api.domain.SysSms;

import java.util.Map;

/**
 * 短信服务
 *
 * @author zy
 */
public interface RemoteSmsService {

    /**
     * 发送短信
     *
     * @param phones     电话号(多个逗号分割)
     * @param templateId 模板id
     * @param param      模板对应参数
     */
    SysSms send(String phones, String templateId, Map<String, String> param) throws ServiceException;

}
