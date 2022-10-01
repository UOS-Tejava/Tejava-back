package com.sogong.tejava.service;

import com.sogong.tejava.dto.RegisterDTO;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.User;
import com.sogong.tejava.repository.UserRepository;
import com.sogong.tejava.util.Const;
import com.sogong.tejava.util.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Objects;

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
        user.setPhoneNo(passwordEncoder.encode(registerDTO.getPhoneNo()));
        user.setRole(Role.USER); // 기본값 : USER
        // TODO: 관리자의 의 경우, Admin role 로 해서 workbench 를 통해 저장할 예정
        // TODO: 연락처 인증 관련해서도 나중에 괜찮다면 작성해볼 것!
        user.setPhoneCheck(registerDTO.getPhoneCheck());
        user.setAgreement(registerDTO.getAgreement());

        // DB에 사용자 저장
        userRepository.save(user);
    }

    public boolean checkUidDuplicate(String uid) {
        return userRepository.existsByUid(uid);
    }

    public User home(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember) {
        // 세션을 가져와 회원을 반환합니다. 프론트에선 반환된 데이터가 null 이면 로그인 페이지를, 아니면 로그인된 페이지를 보여줘야 합니다.
        return loginMember;
    }

    public User login(String uid, String password, Boolean staySignedIn, Boolean loginAsAdmin) { // TODO: 프론트 단에서 하는 게 맞는 지 -> bool 값은 사용할 게 없음
        //TODO: 로그인 시, 관리자의 계정인 경우, 직원 인터페이스 화면으로 이동할 수 있게끔 할 것

        User loginMember = userRepository.findUserByUid(uid);
        log.info(String.valueOf(loginMember));

        // 아이디 존재하지 않는 경우
        if (loginMember == null) { // 받은 uid 로 회원이 존재하는 지 확인
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        if (loginMember.getRole().equals(Role.ADMINISTRATOR) && !loginMember.getPwd().equals(Const.ADMIN_PWD)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
        }

        // 비밀번호가 틀린 경우
        if (loginMember.getRole().equals(Role.USER) && !passwordEncoder.matches(password, loginMember.getPwd())) { // 받은 password 로 기입한 password 와 일치하는 지 확인
            throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
        }

        // TODO: 연락처 인증도 구현 시, phoneCheck 필드 값 확인할 것

        // 신규 세션 생성
        HttpSession session = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        // 세션에 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        log.info("기존의 세션 반환 및 혹은 세션을 생성하였습니다.");
        log.info("해당 세션 : " + session);

        return loginMember;
    }
}
