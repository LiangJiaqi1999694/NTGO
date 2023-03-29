package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("gatewayFlowRuleNacosProvider")
public class GatewayFlowRuleNacosProvider implements DynamicRuleProvider<List<GatewayFlowRuleEntity>> {

    @Autowired
    private NacosConfigUtil nacosConfigUtil;

    @Override
    public List<GatewayFlowRuleEntity> getRules(String appName) throws Exception {
        return nacosConfigUtil.getRuleEntitiesFromNacos(
                appName,
                NacosConfig.GW_FLOW_DATA_ID_POSTFIX,
                GatewayFlowRuleEntity.class
        );
    }
}