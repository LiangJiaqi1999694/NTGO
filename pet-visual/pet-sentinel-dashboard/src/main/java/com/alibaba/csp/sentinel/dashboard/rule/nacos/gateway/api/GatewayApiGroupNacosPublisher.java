package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.api;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("gatewayApiGroupNacosPublisher")
public class GatewayApiGroupNacosPublisher implements DynamicRulePublisher<List<ApiDefinitionEntity>> {

    @Autowired
    private NacosConfigUtil nacosConfigUtil;

    @Override
    public void publish(String app, List<ApiDefinitionEntity> rules) throws Exception {
        nacosConfigUtil.setRuleStringToNacos(
                app,
                NacosConfig.GW_API_GROUP_DATA_ID_POSTFIX,
                rules
        );
    }
}
