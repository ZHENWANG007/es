package com.ems.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 生成jwt令牌相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {


    private String secretKey;
    private long ttl;
    private String tokenName;

}
