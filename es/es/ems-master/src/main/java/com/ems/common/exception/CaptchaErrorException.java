package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * 验证码错误异常
 */
@Getter
public class CaptchaErrorException extends BaseException {

    public CaptchaErrorException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}
