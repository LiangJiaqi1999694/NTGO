package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;
	public static final int NOAUTHENTION_CODE = 100;

	public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
	public static final ReturnT<String> FAIL = new ReturnT<String>(FAIL_CODE, null);
	public static final ReturnT<String> NOAUTHENTION = new ReturnT<String>(NOAUTHENTION_CODE, "未登录");


	private int code;
	private String msg;
	private T content;

	public ReturnT() {
		this.code = SUCCESS_CODE;
		this.msg = "SUCCESS";
	}

	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static ReturnT<String> ok(String msg) {
		ReturnT<String> resp = new ReturnT<>();
		resp.code = SUCCESS_CODE;
		resp.content = null;
		resp.msg = msg;
		return resp;
	}

	public static ReturnT<String> fail(String msg) {
		ReturnT<String> resp = new ReturnT<>();
		resp.code = FAIL_CODE;
		resp.content = null;
		resp.msg = msg;
		return resp;
	}

	public static <T> ReturnT<T> ok(T content) {
		ReturnT<T> resp = new ReturnT<>();
		resp.code = SUCCESS_CODE;
		resp.content = content;
		return resp;
	}

	public ReturnT(int code, T content, String msg) {
		this.code = code;
		this.content = content;
		this.msg = msg;
	}

	public ReturnT(T content) {
		this.code = SUCCESS_CODE;
		this.content = content;
	}

	public ReturnT(T content, String msg) {
		this.code = SUCCESS_CODE;
		this.content = content;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
	}


}
