package com.zfx.community.exception;

/**
 * Created by codedrinker on 2019/5/28.
 */
public class CustomizeException extends RuntimeException {
    private String message;
    private Integer code;//自定义异常 消息和状态码
    public CustomizeException(ICustomizeErrorCode errorCode) {//错误码
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
