package com.pet.common.core.exception.user;

/**
 * 用户密码不正确或不符合规范异常类
 *
 * @author pet
 */
public class UserPasswordNotMatchException extends UserException {
    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException() {
        super("com.pet.common.core.user.password.not.match");
    }
}
