package com.pet.common.oss.exception;

/**
 * OSS异常类
 *
 * @author zy
 */
public class OssException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

}
