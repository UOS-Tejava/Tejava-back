package com.sogong.tejava.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    현재 서버에서 던지는 예외 정리
    1. 규칙 : 내림차순으로 정리하며, 코드도 순서에 맞게 작성할 것!
    2. 마지막 에러는 Exception 으로, 다루지 못한 에러에 대해서도 일관성 있는 data 를 반환하기 위해 작성

    <에러코드 400>
    MethodArgumentNotValidException : DTO 에서 validation 오류

    <에러코드 403>
    AccessDeniedException : 메일 인증 없이 회원가입 요청 / V1의 UOSTime (기존) 회원이어서 메일 인증이 이루어지지 않았던 유저
                            / 로그인 되지 않은 채 서비스(개인정보 수정) 요청

    <에러코드 405>
    MethodNotAllowedException : 잘못된 url 로 api 요청하는 경우

    <에러코드 500>
    IllegalStateException : 폼에서 기존의 회원 정보와 중복 발생 / 비밀번호와 확인용 비밀번호 불일치 / 회원탈퇴 시 비밀번호 틀렸을 때
    IllegalArgumentException : 인증 메일에 사용될 토큰이 없거나 만료 / 없는 tokenID로 회원가입 요청 / 비밀번호 변경 폼에서 기존 비밀번호를 잘못 작성했을 시
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        return ResponseEntity.badRequest().body(new ErrorResponse("400", e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException e) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("403", e.getMessage()));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> methodNotAllowedExceptionHandler(MethodNotAllowedException e) {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse("405", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalStateExceptionHandler(IllegalStateException e) {

        return ResponseEntity.internalServerError().body(new ErrorResponse("500", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> IllegalArgumentExceptionHandler(IllegalArgumentException e) {

        return ResponseEntity.internalServerError().body(new ErrorResponse("500", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse exceptionHandler(Exception e) {

        return new ErrorResponse("Unspecified", e.getMessage());
    }
}
