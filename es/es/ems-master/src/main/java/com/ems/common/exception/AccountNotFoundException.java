package com.ems.common.exception;


import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * 账户不存在异常
 */
@Getter
public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}