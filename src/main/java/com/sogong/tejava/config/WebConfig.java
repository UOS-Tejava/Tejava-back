package com.sogong.tejava.config;

import com.sogong.tejava.util.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor()) //인터셉터 등록. 여기서 LoginCheckInterceptor()은 내가 구현한 클래스 이름이다.
                .order(1) //낮을 수록 먼저 호출
                .addPathPatterns("/**") //인터셉터를 적용할 url 패턴 : 모두
                .excludePathPatterns("/", "/login", "/swagger-ui", "/user/register", "/error"); //인터셉터에서 제외할 패턴 지정 TODO: 비회원 주문 가능하게 패턴 설정할 것!
    }
}
