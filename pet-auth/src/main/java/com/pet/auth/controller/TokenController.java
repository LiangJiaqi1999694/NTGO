package com.pet.auth.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.pet.auth.form.LoginBody;
import com.pet.auth.form.RegisterBody;
import com.pet.auth.form.SmsLoginBody;
import com.pet.auth.service.SysLoginService;
import com.pet.common.core.constant.Constants;
import com.pet.common.core.domain.R;
import com.pet.common.core.exception.user.UserException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * token 控制
 *
 * @author zy
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@Api(tags = "登录方法")
@ApiSupport(order = 1)
public class TokenController {

    private final SysLoginService sysLoginService;

    /**
     * 登录方法
     */
    @PostMapping("login")
    @ApiOperationSupport(order = 1)
    @ApiOperation("登录")
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody form) {
        String accessToken = null;
        try {
            // 用户登录
            accessToken = sysLoginService.login(form.getUsername(), form.getPassword());
        }catch (UserException e){
            log.error(e.getMessage());
            throw e;
        }
        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<>();
        rspMap.put(Constants.ACCESS_TOKEN, accessToken);
        return R.ok(rspMap);
    }

    /**
     * 短信登录
     *
     * @param smsLoginBody 登录信息
     * @return 结果
     */
    @PostMapping("/smsLogin")
    @ApiOperationSupport(order = 2)
    @ApiOperation("短信登录")
    public R<Map<String, Object>> smsLogin(@Validated @RequestBody SmsLoginBody smsLoginBody) {
        Map<String, Object> ajax = new HashMap<>();
        // 生成令牌
        String token = sysLoginService.smsLogin(smsLoginBody.getPhonenumber(), smsLoginBody.getSmsCode());
        ajax.put(Constants.ACCESS_TOKEN, token);
        return R.ok(ajax);
    }

    /**
     * 小程序登录(示例)
     *
     * @param xcxCode 小程序code
     * @return 结果
     */
    @PostMapping("/xcxLogin")
    @ApiOperationSupport(order = 3)
    @ApiOperation("小程序登录")
    public R<Map<String, Object>> xcxLogin(@NotBlank(message = "{xcx.code.not.blank}") String xcxCode) {
        Map<String, Object> ajax = new HashMap<>();
        // 生成令牌
        String token = sysLoginService.xcxLogin(xcxCode);
        ajax.put(Constants.ACCESS_TOKEN, token);
        return R.ok(ajax);
    }

    /**
     * 登出方法
     */
    @DeleteMapping("logout")
    @ApiOperation("登出方法")
    @ApiOperationSupport(order = 4)
    public R<Void> logout() {
        sysLoginService.logout();
        return R.ok();
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("register")
    @ApiOperationSupport(order = 5)
    public R<Void> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        sysLoginService.register(registerBody);
        return R.ok();
    }

}
