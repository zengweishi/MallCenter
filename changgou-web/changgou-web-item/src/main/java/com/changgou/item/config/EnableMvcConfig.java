package com.changgou.item.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/2 11:22
 * @Description:静态资源过滤
 */
@ControllerAdvice
@Configuration
public class EnableMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //所有以/items/**的请求，都直接到classpath:/templates/items/中找文件
        registry.addResourceHandler("/item/**").addResourceLocations("classpath:/templates/items/");
    }
}
