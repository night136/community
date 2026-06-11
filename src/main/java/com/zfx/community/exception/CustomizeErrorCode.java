package com.zfx.community.exception;

/**
 * 自定义错误码枚举
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "该问题不存在"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "请先登录后再操作"),
    SYS_ERROR(2004, "服务器异常，请稍后再试"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在"),
    CONTENT_IS_EMPTY(2007, "评论内容不能为空"),
    INVALID_INPUT(2008, "输入参数不合法"),
    INVALID_OPERATION(2009, "无权执行此操作"),
    NOTIFICATION_NOT_FOUND(2010, "通知不存在"),
    READ_NOTIFICATION_FAIL(2011, "通知读取失败"),
    NOTIFICATION_NOT_YOURS(2012, "只能操作自己的通知");

    private final Integer code;
    private final String message;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
