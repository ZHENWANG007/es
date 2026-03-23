package com.ems.common.config;

import com.ems.common.properties.AliOssProperties;
import com.ems.common.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AilOssUtil对象
 **/
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean // 将AliOssUtil注册为Spring的Bean
    @ConditionalOnMissingBean // 如果容器中没有AliOssUtil对象，则创建
    /*
      在aliOssUtil方法中，AliOssUtil对象通过构造函数注入的方式被创建，并将AliOssProperties中的值注入到AliOssUtil的属性中。
     */
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}