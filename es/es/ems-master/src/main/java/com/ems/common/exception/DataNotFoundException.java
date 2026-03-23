package com.ems.common.exception;

import com.ems.common.enumerate.ResultEnum;

/**
 * 查询数据为空异常
 */
public class DataNotFoundException extends BaseException {

    public DataNotFoundException(ResultEnum resultEnum) {
        super(resultEnum);
    }

}