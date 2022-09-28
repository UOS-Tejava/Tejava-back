package com.sogong.tejava.service;

import com.sogong.tejava.dto.RegisterDTO;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.User;
import com.sogong.tejava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 등록
    public void registerUser(RegisterDTO registerDTO) {


        if (!registerDTO.getPwd().equals(registerDTO.getMatchingPwd())) {
            throw new IllegalStateException("비밀번호가 서로 일치하지 않습니다.");
        } else if (checkUidDuplicate(registerDTO.getUid())) {
            throw new IllegalStateException("이미 사용 중인 아이디가 있습니다.");
        }

        // 회원가입 폼에서 입력받은 정보로 DTO 객체에 저장
        User user = new User();
        user.setUid(registerDTO.getUid());
        user.setPwd(passwordEncoder.encode(registerDTO.getPwd()));
        user.setName(registerDTO.getName());
        user.setAddress(registerDTO.getAddress());
        user.setRole(Role.USER); // 기본값 : USER
        // TODO: 관리자의 의 경우, Admin role 로 해서 workbench 를 통해 저장할 예정
        // TODO: 전화번호 인증 관련해서도 나중에 괜찮다면 작성해볼 것!
        user.setAgreement(registerDTO.getAgreement());

        // DB에 사용자 저장
        userRepository.save(user);
    }
    public boolean checkUidDuplicate(String uid) {
        return userRepository.existsByUid(uid);
    }
}
