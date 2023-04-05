package com.pet.common.doc.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.pet.common.doc.config.properties.SwaggerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 * Swagger 文档配置
 *
 * @author zy
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
@EnableKnife4j
@EnableSwagger2WebFlux
@Import(BeanValidatorPluginsConfiguration.class)
@Slf4j
public class SwaggerAutoConfiguration {

    private final SwaggerProperties swaggerProperties;
    @Value("${spring.application.name}")
    private String application;
    @Bean
    public Docket productApi() {
        log.info("===================================productApi==================");
        return new Docket(DocumentationType.OAS_30)

            .apiInfo(apiInfo())
            .select()
//            .paths()
            .apis(RequestHandlerSelectors.basePackage("com.pet"))
//            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
            .paths(PathSelectors.any())
            .build()
            .pathMapping(application)
//            .securityContexts(securityContexts())
//            .securitySchemes(securitySchemes())
            ;
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
            // 设置标题
            .title(swaggerProperties.getInfo().getTitle())
            // 描述
            .description(swaggerProperties.getInfo().getDescription() + "接口文档")
            // 作者信息
            .contact(new Contact("erp", "http://www.erp.com/", "1029861695@qq.com"))
            // 版本
            .version("版本号:" + swaggerProperties.getInfo().getVersion())
            .build();
    }

}
