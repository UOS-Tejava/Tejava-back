package com.sogong.tejava.service;

import com.sogong.tejava.dto.ChangeOrderStatusDTO;
import com.sogong.tejava.dto.UserIdDTO;
import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.OrderStatus;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.repository.OrderRepository;
import com.sogong.tejava.repository.StockRepository;
import com.sogong.tejava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    /*
    1. 들어온 주문 조회
    2. 주문의 상태 변경하기(pending, cooking, delivering, completed)
    3. 재고 현황 보여주기
    (4. 관리자 권한이 있는 지 확인하기)
     */

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployeeService(StockRepository stockRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 모든 주문 조회
    public List<Order> getOrderList(UserIdDTO userIdDTO) {

        userRoleCheck(userIdDTO.getUserId());
        return orderRepository.findAll();
    }

    // 주문 상태 변경하기
    public void updateOrderStatus(UserIdDTO userIdDTO, ChangeOrderStatusDTO changeOrderStatusDTO) {

        userRoleCheck(userIdDTO.getUserId());

        Order order = orderRepository.findOrderById(changeOrderStatusDTO.getOrderId());

        if (order == null) {
            throw new IllegalStateException("선택하신 주문이 존재하지 않습니다.");
        }

        order.setOrder_status(changeOrderStatusDTO.getOrderStatus());

        // 비회원 주문이었고, 배달 완료 상태로 바꾼다면 비회원을 테이블에서 삭제! -> cascade 로 장바구니도 사라짐
        if(changeOrderStatusDTO.getOrderStatus().equals(OrderStatus.completed)) {
            if(userRepository.findUserById(userIdDTO.getUserId()).getRole().equals(Role.NOT_MEMBER)) {
                userRepository.delete(userRepository.findUserById(userIdDTO.getUserId()));
            }
        }
        orderRepository.save(order);
    }

    // 재고 현황 보여주기
    public List<StockItem> showStockInfo(UserIdDTO userIdDTO) {

        userRoleCheck(userIdDTO.getUserId());
        return stockRepository.findAll();
    }

    // 관리자 권한이 있는 지 확인하기
    public void userRoleCheck(Long userId) {

        User user = userRepository.findUserById(userId);

        if (!user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("관리자 외 접근이 불가능한 페이지입니다.\n직원이시라면 접근 권한 신청 부탁드립니다.");
        }
    }
}
