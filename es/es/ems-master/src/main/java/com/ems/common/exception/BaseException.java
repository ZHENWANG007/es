package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * 基础业务异常类，所有自定义异常的父类
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final ResultEnum resultEnum;

    public BaseException(ResultEnum resultEnum) {
        super(resultEnum.getMessage()); // 设置异常的详细信息
        this.resultEnum = resultEnum;
    }
}
