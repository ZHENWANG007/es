package com.ems.handler;

import com.ems.common.enumerate.ResultEnum;
import com.ems.common.exception.BaseException;
import com.ems.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex){

        // 如果是访问拒绝异常，不处理，让SecurityConfig中配置的处理器处理
        if(ex instanceof AccessDeniedException) {

            throw (AccessDeniedException)ex;

        }

        log.error("全局异常信息：{}", ex.getMessage());
        return Result.error(StringUtils.hasLength(ex.getMessage()) ? ex.getMessage() : "操作失败");
    }

    /**
     * 捕获业务异常
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("业务异常信息：{}", ex.getResultEnum().message());
        return Result.error(ex.getResultEnum());
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // 错误信息：Duplicate entry 'zhaosi' for key 'employee.idx_username' -- > 用户ID重复
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + ResultEnum.USER_NAME_HAS_EXISTED.message();
            return Result.error(msg);
        }else {
            return Result.error(ResultEnum.UNKNOWN_ERROR);
        }

    }
}
