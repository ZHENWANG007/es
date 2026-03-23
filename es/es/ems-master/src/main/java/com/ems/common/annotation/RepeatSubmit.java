package com.ems.common.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交注解
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 锁定时间，默认5000毫秒
     */
    int interval() default 5000;

    /**
     * 锁定时间单位，默认毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 提示信息
     */
    String message() default "不允许重复提交，请稍后再试！";

}
