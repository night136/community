package com.zfx.community.exception;

/**
 * 自定义异常
 */
public class CustomizeException extends RuntimeException {

    private final Integer code;
    private final String message;

    public CustomizeException(ICustomizeErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
