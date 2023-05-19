package com.xxl.job.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author majun
 * @description
 * @date 2020/5/12
 */
@SpringBootApplication(scanBasePackages = {"com.xxl.job","com.warrior.xxl"})
@EnableDiscoveryClient
public class XxlJobSpringCloudExecutorApp {
    public static void main(String[] args) {
        SpringApplication.run(XxlJobSpringCloudExecutorApp.class, args);
    }
}
