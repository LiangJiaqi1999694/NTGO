package com.alibaba.csp.sentinel.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("degradeRuleNacosPublisher")
public class DegradeRuleNacosPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {

    @Autowired
    private NacosConfigUtil nacosConfigUtil;

    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) throws Exception {
        nacosConfigUtil.setRuleStringToNacos(
                app,
                NacosConfig.DEGRADE_DATA_ID_POSTFIX,
                rules
        );
    }
}
