package com.oil.config;

import com.oil.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * ClassName: MvcConfig
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2023/12/13
 * @Version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/user/login",
                        "/user/info"
                ).order(0);
    }
}
