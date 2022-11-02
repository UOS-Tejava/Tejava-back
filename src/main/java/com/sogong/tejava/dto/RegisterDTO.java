package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO { // 회원가입 폼

    @NotBlank(message = "아이디를 기입해주세요.")
    private String uid;

    @NotBlank(message = "성명을 기입해주세요.")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[~!@#$%^&*<>?/`()_+-='\"{}\\[\\]|]).{8,20}$", message = "비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String pwd; // 비밀번호

    @NotBlank(message = "비밀번호를 다시 한 번 입력해주세요.")
    private String matchingPwd; // 비밀번호 확인

    @NotBlank(message = "연락처를 입력해주세요.")
    private String phoneNo;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    private Boolean phoneCheck;
    private Boolean agreement = true; // 개인정보(성명, 주소) 수집 이용 동의(default : true)
    private Boolean RegisterAsAdmin = false; // 관리자로 회원가입하기(default : user)

    private String adminVerificationCode; // tejava
}
