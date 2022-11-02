package com.sogong.tejava.service;

import com.sogong.tejava.dto.RegisterDTO;
import com.sogong.tejava.dto.UserDTO;
import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.OrderHistory;
import com.sogong.tejava.entity.customer.ShoppingCart;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.repository.*;
import com.sogong.tejava.util.Const;
import com.sogong.tejava.util.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;

    // 회원을 위한 장바구니 테이블 생성
    public void createCartTb(User user) {
        ShoppingCart shoppingCart = ShoppingCart.createCart(user);
        user.setShoppingCart(shoppingCart);
        shoppingCartRepository.save(shoppingCart);
    }

    // 회원을 위한 주문테이블 생성
    public void createOrderTb(User user) {
        Order order = Order.createOrder(user);
        orderRepository.save(order);
    }

    // 회원을 위한 주문 내역 테이블 생성
    public void createOrderHistoryTb(User user) {

        OrderHistory orderHistory = OrderHistory.createOrderHistory(user);
        user.setOrderHistory(orderHistory);
        orderHistoryRepository.save(orderHistory);
    }

    // 회원가입
    public void registerUser(RegisterDTO registerDTO) { // TODO: 연락처 인증 관련해서 모든 개발 완료 이후 우선순위에 따라 추가해볼 것 using coolSMS

        if (!registerDTO.getPwd().equals(registerDTO.getMatchingPwd())) {
            throw new IllegalStateException("비밀번호가 서로 일치하지 않습니다.");
        } else if (checkUidDuplicate(registerDTO.getUid())) {
            throw new IllegalStateException("이미 사용 중인 아이디가 있습니다.");
        }

        User user = new User();

        if (registerDTO.getRegisterAsAdmin()) {
            if (!registerDTO.getAdminVerificationCode().equals(Const.ADMIN_REGISTER_VERIFICATION_CODE)) {
                throw new IllegalStateException("잘못된 인증코드입니다.");
            }
            user.setRole(Role.ADMINISTRATOR);
        } else {
            user.setRole(Role.USER);
        }

        // 회원가입 폼에서 입력받은 정보로 DTO 객체에 저장
        user.setUid(registerDTO.getUid());
        user.setPwd(passwordEncoder.encode(registerDTO.getPwd()));
        user.setName(registerDTO.getName());
        user.setAddress(registerDTO.getAddress());
        user.setPhoneNo(passwordEncoder.encode(registerDTO.getPhoneNo()));
        user.setPhone_check(registerDTO.getPhoneCheck());
        user.setAgreement(registerDTO.getAgreement());
        userRepository.save(user);

        createCartTb(user);
        createOrderHistoryTb(user);
        createOrderTb(user);

        // DB에 저장
        userRepository.save(user);
    }

    public boolean checkUidDuplicate(String uid) {
        return userRepository.existsByUid(uid);
    }

    public UserDTO home(HttpServletRequest request) {
        // 세션을 가져와 회원을 반환합니다. 반환된 회원이 없다면 비회원을 생성하여 반환

        HttpSession currentSession = request.getSession(false);

        if (currentSession == null) {
            // 신규 세션 생성
            HttpSession notMemberSession = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession();

            User notMember = new User();

            // 비회원을 위한 장바구니 생성
            ShoppingCart.createCart(notMember);

            // 비회원 유저 세팅
            notMember.setShoppingCart(shoppingCartRepository.findByUserId(notMember.getId()));
            notMember.setRole(Role.NOT_MEMBER);
            notMember.setName("비회원");

            // 세션에 비회원 정보 보관
            notMemberSession.setAttribute(SessionConst.NOT_MEMBER, notMember);

            // 비회원 db에 저장
            userRepository.save(notMember);

            return null;
        }

        return UserDTO.from((User) currentSession.getAttribute(SessionConst.LOGIN_MEMBER));
    }

    public UserDTO login(String uid, String password, Boolean staySignedIn) { // TODO: bool 값은 사용하고 있지 않다는 거 체크할 것

        User loginMember = userRepository.findUserByUid(uid);
        log.info(String.valueOf(loginMember));

        // 아이디 존재하지 않는 경우
        if (loginMember == null) { // 받은 uid 로 회원이 존재하는 지 확인
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        // 관리자/일반 테스트/일반 유저 로그인
        // TODO : 개발 완료되는 경우, 테스트 계정 삭제해야 함 (아이디 겹칠 수 있기 때문)
        if (loginMember.getRole().equals(Role.ADMINISTRATOR)) { // 관리자의 경우
            if (loginMember.getUid().equals(Const.TEST_ADMIN_UID)) { // 테스트 계정의 경우
                if (!password.equals(Const.TEST_PWD)) {
                    throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
                }
            } else { // 테스트 계정이 아닌 경우
                if (!passwordEncoder.matches(password, loginMember.getPwd())) {
                    throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
                }
            }
        } else {
            if (loginMember.getUid().equals(Const.TEST_USER_UID)) { // 일반 사용자의 경우
                if (!password.equals(Const.TEST_PWD)) { // 테스트 계정의 경우
                    throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
                }
            } else { // 테스트 계정이 아닌 경우
                if (!passwordEncoder.matches(password, loginMember.getPwd())) {
                    throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
                }
            }
        }

        // 홈화면 들어갈 시 생성되었던 세션 삭제
        HttpSession notMemberSession = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession(false);
        if (notMemberSession != null) {
            notMemberSession.invalidate();
        }

        // 신규 세션 생성
        HttpSession session = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession();

        // 세션에 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        log.info("기존의 세션 반환 및 혹은 세션을 생성하였습니다.");
        log.info("해당 세션 : " + session);

        return UserDTO.from(loginMember);
    }
}
