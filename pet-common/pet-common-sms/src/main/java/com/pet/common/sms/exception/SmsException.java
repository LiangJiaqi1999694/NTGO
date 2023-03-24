package com.pet.common.sms.exception;

/**
 * Sms异常类
 *
 * @author zy
 */
public class SmsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SmsException(String msg) {
        super(msg);
    }

}
