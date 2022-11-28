package com.sogong.tejava.validate.config.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // String requestURI = request.getRequestURI(); -> 나중에 redirect 가능!
        HttpSession session = request.getSession(false); // 세션 가져옴
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) { // 세션에서 회원의 정보가 없을 시 에러 던짐
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "로그인 후, 사용해주세요.");
            return false;
        }
        return true;
    }
}