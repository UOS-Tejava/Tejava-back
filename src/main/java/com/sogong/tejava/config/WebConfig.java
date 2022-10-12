package com.sogong.tejava.config;

import com.sogong.tejava.util.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor()) // 인터셉터 등록
                .order(1) // 낮을 수록 먼저 호출
                .addPathPatterns("/**") //인터셉터를 적용할 url 패턴 : 모두
                .excludePathPatterns("/", "/login", "/swagger-ui/index.html", "/webjars/**,", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/error");
    }
}
