package com.pet.gateway.filter;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.pet.common.core.constant.HttpStatus;
import com.pet.gateway.config.properties.CertIgnoreWhiteProperties;
import com.pet.gateway.config.properties.IgnoreWhiteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Sa-Token 权限认证] 拦截器
 *
 * @author zy
 */
@Slf4j
@Configuration
public class AuthFilter {

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter(IgnoreWhiteProperties ignoreWhite, CertIgnoreWhiteProperties certIgnoreWhite) {
        return new SaReactorFilter()
            // 拦截地址
            .addInclude("/**")
            // 开放地址
            .addExclude("/favicon.ico", "/actuator/**","/doc.html","/swagger-ui/**")
            .addExclude("/webjars/**", "/swagger-resources/**", "/v3/**")
            // 鉴权方法：每次访问进入
            .setAuth(obj -> {
                // 登录校验 -- 拦截所有路由
                SaRouter.match("/**")
                    .notMatch(ignoreWhite.getWhites())
                    .notMatch(certIgnoreWhite.getWhites())
                    .check(r -> {
                        // 检查是否登录 是否有token
                        StpUtil.checkLogin();

                        // 有效率影响 用于临时测试
                        if (log.isDebugEnabled()) {
                            log.debug("剩余有效时间: {}", StpUtil.getTokenTimeout());
                            log.debug("临时有效时间: {}", StpUtil.getTokenActivityTimeout());
                        }
                    });
                // 证书鉴权
                SaRouter.match(certIgnoreWhite.getWhites())
                    .check(r ->{


                    });
            }).setError(e -> SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED));
    }
}
