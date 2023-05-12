package com.pet.auth.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * author zy
 * 证书鉴权
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@Api(tags = "证书鉴权")
@ApiSupport(order = 1)
public class CertController {



}
