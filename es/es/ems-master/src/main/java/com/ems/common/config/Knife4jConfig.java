package com.ems.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 蒋银辉
 * @version 1.0
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(
                        new Info().title("接口文档")
                                // 接口文档简介
                                .description("企业员工管理系统")
                                // 接口文档版本
                                .version("v1.0")
                                // 开发者联系方式
                                .contact(new Contact().name("励碼").email("1744211486@qq.com"))
                );
    }
}