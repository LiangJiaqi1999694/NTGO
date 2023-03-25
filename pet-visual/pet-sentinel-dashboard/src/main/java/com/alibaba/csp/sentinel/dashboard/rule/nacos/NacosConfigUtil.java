package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class NacosConfigUtil {

    @Autowired
    private ConfigService configService;
    @Value("${nacos.group}")
    public String NACOS_GROUP;

    private NacosConfigUtil() {}

    /**
     * 将规则序列化成JSON文本，存储到Nacos server中
     *
     * @param app           应用名称
     * @param postfix       规则后缀 eg.NacosConfigUtil.FLOW_DATA_ID_POSTFIX
     * @param rules         规则对象
     * @throws NacosException 异常
     */
    public <T> void setRuleStringToNacos(String app, String postfix, List<T> rules) throws NacosException {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }

        String dataId = genDataId(app, postfix);
        // 存储
        configService.publishConfig(
                dataId,
                NACOS_GROUP,
                JSON.toJSONString(rules)
        );
    }

    /**
     * 从Nacos server中查询响应规则，并将其反序列化成对应Rule实体
     *
     * @param appName       应用名称
     * @param postfix       规则后缀 eg.NacosConfigUtil.FLOW_DATA_ID_POSTFIX
     * @param clazz         类
     * @param <T>           泛型
     * @return 规则对象列表
     * @throws NacosException 异常
     */
    public <T> List<T> getRuleEntitiesFromNacos(String appName, String postfix, Class<T> clazz) throws NacosException {
        String dataId = genDataId(appName, postfix);
        String rules = configService.getConfig(
                dataId,
                NACOS_GROUP,
                3000
        );
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(rules, clazz);
    }

    private static String genDataId(String appName, String postfix) {
        return appName + postfix;
    }
}
