package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * 用户注册失败
 */
@Getter
public class AccountRegisterFailException extends BaseException{

    public AccountRegisterFailException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}
