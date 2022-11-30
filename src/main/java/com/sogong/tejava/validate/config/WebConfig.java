package com.sogong.tejava.validate.config;

import com.sogong.tejava.validate.config.util.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor()) // 인터셉터 등록
                .order(1) // 낮을 수록 먼저 호출
                .addPathPatterns("/**") //인터셉터를 적용할 url 패턴 : 모두
                .excludePathPatterns("/", "/login", "/swagger-ui/index.html", "/webjars/**,", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/user/register","/user/duplication-check/**", "/cart/**", "/order/**", "/error");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/**", "http://localhost:8080/**", "http://43.200.93.146:8000/**", "http://43.200.93.146/**", "http://43.200.93.146:3000/**")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");
    }
}