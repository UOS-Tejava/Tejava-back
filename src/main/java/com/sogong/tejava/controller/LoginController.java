package com.sogong.tejava.controller;

import com.sogong.tejava.dto.LoginDTO;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.service.UserService;
import com.sogong.tejava.util.SessionConst;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @ApiOperation(value = "홈 화면", notes = "첫 화면입니다. 세션을 가져와 회원을 반환합니다.")
    public ResponseEntity<User> home(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember) {
        return ResponseEntity.ok().body(userService.home(loginMember));
    }

    @GetMapping("/login")
    @ApiOperation(value = "로그인", notes = "아이디와 비밀번호로 로그인합니다.")
    public ResponseEntity<User> login(@Validated @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok().body(userService.login(loginDTO.getUid(), loginDTO.getPwd(), loginDTO.getStaySignedIn()));
    }

//    @Async
//    @GetMapping("/email/uid/{portalID}")
//    @ApiOperation(value = "아이디를 모르는 경우", notes = "포털아이디를 입력받아 회원의 uid를 포함한 메일을 전송합니다.")
//    public CompletableFuture<ResponseEntity<?>> sendMailWithUid(@PathVariable String portalID) throws MessagingException {
//
//        return CompletableFuture.completedFuture(userService.sendEmailWithUid(portalID));
//    }
//
//    @Async
//    @GetMapping("/email/password/{uid}/{portalID}")
//    @ApiOperation(value = "비밀번호를 모르는 경우", notes = "UOSTime 아이디와 포털 아이디를 입력받아 임시 비밀번호 포함한 메일을 전송합니다.")
//    public CompletableFuture<ResponseEntity<?>> sendEmailWithTempPw(@PathVariable String uid, @PathVariable String portalID) throws MessagingException {
//
//        return CompletableFuture.completedFuture(userService.sendEmailWithTempPw(uid, portalID));
//    }
}
