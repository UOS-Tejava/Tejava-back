package com.sogong.tejava.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    <에러코드 405>
    MethodNotAllowedException : 잘못된 url 로 api 요청하는 경우

    <에러코드 500>
    IllegalStateException : 폼에서 기존의 회원 정보와 중복 발생 / 비밀번호와 확인용 비밀번호 불일치 / 회원탈퇴 시 비밀번호 틀렸을 때 / 접수 대기 중 상태아닌데 주문 수정을 할 때
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        return ResponseEntity.badRequest().body(new ErrorResponse("400", "Method Argument Not Valid Error", e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> methodNotAllowedExceptionHandler(MethodNotAllowedException e) {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse("405", "Method Not Allowed Error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalStateExceptionHandler(IllegalStateException e) {

        return ResponseEntity.internalServerError().body(new ErrorResponse("500", "Illegal State Error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> IllegalArgumentExceptionHandler(IllegalArgumentException e) {

        return ResponseEntity.internalServerError().body(new ErrorResponse("500", "Illegal Argument Error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse exceptionHandler(Exception e) {

        return new ErrorResponse("Unspecified", "Unspecified error", e.getMessage());
    }
}
