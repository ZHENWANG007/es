package com.ems.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置类
 */
@Data
@Component  //交给IOC容器管理
@ConfigurationProperties(prefix = "aliyun.oss") //指定配置项前缀
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
