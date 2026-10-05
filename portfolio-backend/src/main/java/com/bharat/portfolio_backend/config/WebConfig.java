package com.bharat.portfolio_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebConfig implements WebMvcConfigurer {

    private final AdminKeyInterceptor adminKeyInterceptor;

    public WebConfig(AdminKeyInterceptor adminKeyInterceptor) {
        this.adminKeyInterceptor = adminKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminKeyInterceptor).addPathPatterns("/v1/**");
    }
}