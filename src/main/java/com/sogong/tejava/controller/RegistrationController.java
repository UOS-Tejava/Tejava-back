package com.sogong.tejava.controller;

import com.sogong.tejava.domain.dto.RegisterDTO;
import com.sogong.tejava.domain.dto.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // 중복 에러(아이디)가 없거나 비밀번호와 재확인용 비밀번호가 일치하면 등록 진행, 유효성 검사는 이미 적용되어 있음
    @PostMapping("user/register")
    @ApiOperation(value = "회원 가입", notes = "회원가입을 진행합니다. \n \n" +
            "모든 필드 값은 null 이 될 수 없습니다. \n" +
            "비밀번호는 8~20자로, 영문 대 소문자, 숫자, 그리고 특수문자가 포함되어야 합니다.")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegisterDTO registerDTO) {

        userService.registerUser(registerDTO);
        return ResponseEntity.ok().build();
    }

    // 아이디 중복 확인
    @GetMapping("user/duplication-check/uid/{uid}")
    @ApiOperation(value = "중복 체크", notes = "회원 가입 시 기입한 아이디가 중복되는 지 확인합니다.")
    public ResponseEntity<Boolean> checkUidDuplicate(@PathVariable("uid") String uid) {
        return ResponseEntity.ok().body(userService.checkUidDuplicate(uid));
    }
}