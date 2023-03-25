package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class NacosConfig {

    @Value("${nacos.server-addr}")
    private String serverAddr;

    @Value("${nacos.namespace}")
    private String namespace;

    @Value("${nacos.username}")
    private String username;

    @Value("${nacos.password}")
    private String password;

    /**
     * app...
     */
    public static final String FLOW_DATA_ID_POSTFIX = "-flow-rules";
    public static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules";
    public static final String SYSTEM_DATA_ID_POSTFIX = "-system-rules";
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "-param-flow-rules";
    public static final String AUTHORITY_DATA_ID_POSTFIX = "-authority-rules";

    /**
     * gateway
     */
    public static final String GW_API_GROUP_DATA_ID_POSTFIX = "-gw-api-group-rules";
    public static final String GW_FLOW_DATA_ID_POSTFIX = "-gw-flow-rules";

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, this.serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, this.namespace);
//        properties.put(PropertyKeyConst.GROUP, this.group);
        properties.put(PropertyKeyConst.USERNAME, this.username);
        properties.put(PropertyKeyConst.PASSWORD, this.password);
        return ConfigFactory.createConfigService(properties);
    }
}
