package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 提交或访问频繁异常
 */
@Getter
@Setter
public class GeneralBusinessException extends BaseException{

    public GeneralBusinessException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}
