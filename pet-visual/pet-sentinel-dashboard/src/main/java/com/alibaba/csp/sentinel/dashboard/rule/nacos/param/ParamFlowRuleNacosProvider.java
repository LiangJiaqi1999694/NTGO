package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("paramFlowRuleNacosProvider")
public class ParamFlowRuleNacosProvider implements DynamicRuleProvider<List<ParamFlowRuleEntity>> {
    @Autowired
    private NacosConfigUtil nacosConfigUtil;

    @Override
    public List<ParamFlowRuleEntity> getRules(String appName) throws Exception {
        return nacosConfigUtil.getRuleEntitiesFromNacos(
                appName,
                NacosConfig.PARAM_FLOW_DATA_ID_POSTFIX,
                ParamFlowRuleEntity.class
        );
    }
}
