package com.pet.gateway;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 网关启动程序
 *
 * @author pet
 */
@EnableKnife4j
@EnableSwagger2
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PetGatewayApplication {
    public static void main(String[] args) {
        // 标记 sentinel 类型为 网关
        System.setProperty("csp.sentinel.app.type", "1");
        SpringApplication application = new SpringApplication(PetGatewayApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  网关启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
