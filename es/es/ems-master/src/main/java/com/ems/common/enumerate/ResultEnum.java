package com.ems.common.enumerate;

import lombok.Getter;

/**
 * 统一响应状态码枚举类
 */
@Getter
public enum ResultEnum {

    /* 成功状态码 */
    SUCCESS(1, "操作成功！"),

    /* 错误状态码 */
    FAIL(0, "操作失败！"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_BLANK(10002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(10003, "参数格式错误"),
    PARAM_NOT_COMPLETE(10004, "参数缺失"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录，请先登录"),
    USER_PASSWORD_ERROR(20002, "密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),
    USER_NAME_HAS_EXISTED(20006, "用户名已存在"),
    USER_CAPTCHA_ERROR(20007, "验证码错误"),
    USER_CAPTCHA_NOT_EXIST(20008, "验证码不存在或已过期"),
    USER_SMS_SEND_LIMIT_EXCEEDED(20009, "发送次数过多，请24H后再试"),
    USER_SMS_SEND_TOO_FREQUENTLY(20010, "发送短信过于频繁，请稍后再试"),
    USER_SMS_SEND_FAILED(20011, "发送短信失败"),
    USER_REGISTER_FAIL(20012, "用户注册失败"),
    USER_REPEAT_SUBMIT(20013, "不允许重复提交，请稍后再试！"),

    /* 系统错误：40001-49999 */
    FILE_MAX_SIZE_OVERFLOW(40003, "上传尺寸过大"),
    FILE_ACCEPT_NOT_SUPPORT(40004, "上传文件格式不支持"),
    FILE_UPLOAD_FAILED(40005, "文件上传失败"),
    ACCESS_LIMIT_REACHED(40006, "访问频率超过限制"),
    UNKNOWN_ERROR(49999, "未知错误"),


    /* 数据错误：50001-599999 */
    RESULT_DATA_NONE(50001, "查询数据为空"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),
    AUTH_CODE_ERROR(50004, "验证码错误"),


    /* 权限错误：70001-79999 */
    PERMISSION_UNAUTHENTICATED(70001, "此操作需要登陆系统！"),

    PERMISSION_UNAUTHORIZED(70002, "权限不足，无权操作！"),

    PERMISSION_EXPIRE(70003, "登录状态过期！"),

    PERMISSION_TOKEN_EXPIRED(70004, "token已过期"),

    PERMISSION_LIMIT(70005, "访问次数受限制"),

    PERMISSION_TOKEN_INVALID(70006, "无效token"),

    PERMISSION_SIGNATURE_ERROR(70007, "签名失败");

    // 状态码
    final int code;
    // 提示信息
    final String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}