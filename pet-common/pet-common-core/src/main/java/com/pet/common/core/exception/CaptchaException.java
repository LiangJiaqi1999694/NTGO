package com.pet.common.core.exception;

import com.pet.common.core.exception.user.UserException;

/**
 * 验证码错误异常类
 *
 * @author zy
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("验证码错误");
    }

    public CaptchaException(String msg) {
        super(msg);
    }
}
