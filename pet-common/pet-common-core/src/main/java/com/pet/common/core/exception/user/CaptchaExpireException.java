package com.pet.common.core.exception.user;

/**
 * 验证码失效异常类
 *
 * @author pet
 */
public class CaptchaExpireException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("com.pet.common.core.user.jcaptcha.expire");
    }
}
