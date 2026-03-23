package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException {

    public PasswordErrorException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}