package com.sogong.tejava.controller;

import com.sogong.tejava.dto.LoginDTO;
import com.sogong.tejava.dto.UserDTO;
import com.sogong.tejava.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    @ApiOperation(value = "홈화면", notes = "홈 화면입니다. 세션이 없다면 생성 이후, 비회원을 세션에 저장합니다.")
    @ApiResponse(responseCode = "200", description = "세션을 가져와 (비)회원을 반환합니다. 비회원의 경우, 주소(address)와 주문횟수(order_cnt)는 null 값이 들어갑니다.", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    public ResponseEntity<?> home(HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.home(request));
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "아이디와 비밀번호로 로그인합니다.")
    public ResponseEntity<UserDTO> login(HttpServletRequest request, @Validated @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok().body(userService.login(request, loginDTO.getUid(), loginDTO.getPwd(), loginDTO.getStaySignedIn()));
    }
}