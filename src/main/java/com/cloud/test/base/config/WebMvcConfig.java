package com.cloud.test.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${image.storage.path}")
    private String imageStoragePath;

    @Value("${image.storage.access-prefix}")
    private String imageAccessPrefix;

    /**
     * 配置静态资源映射，让前端能访问本地图片
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /images/** 路径映射到本地图片存储路径
        registry.addResourceHandler(imageAccessPrefix + "**")
                .addResourceLocations("file:" + imageStoragePath);
    }
}