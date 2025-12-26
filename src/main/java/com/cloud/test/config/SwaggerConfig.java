package com.cloud.test.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户管理系统API文档")
                        .description("这是一个基于SpringBoot 3.x的用户管理系统API文档，包含用户CRUD、权限管理等接口")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("技术部")
                                .email("asta.guo.cn@gmail.com")
                                .url("https://www.guo.cn")
                        )
                );
    };
}
