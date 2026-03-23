package com.ems.common.exception;


import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * 账户锁定异常
 */
@Getter
public class AccountForbiddenException extends BaseException {

    public AccountForbiddenException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}
