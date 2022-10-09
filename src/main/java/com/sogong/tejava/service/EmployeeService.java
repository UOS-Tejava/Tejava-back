package com.sogong.tejava.service;

import com.sogong.tejava.dto.ChangeOrderStatusDTO;
import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.repository.OrderRepository;
import com.sogong.tejava.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    /*
    1. 들어온 주문 조회
    2. 주문의 상태 변경하기(pending, cooking, delivering, completed)
    3. 재고 현황 보여주기
    (4. 관리자 권한이 있는 지 확인하기)
     */

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

    // 모든 주문 조회
    public List<Order> getOrderList(User user) {
        validateUser(user);
        return orderRepository.findAll();
    }

    // 주문 상태 변경하기
    public void updateOrderStatus(User user, ChangeOrderStatusDTO changeOrderStatusDTO) {
        validateUser(user);
        Order order = orderRepository.findOrderById(changeOrderStatusDTO.getOrderId());

        if(order == null) {
            throw new IllegalStateException("선택하신 주문이 존재하지 않습니다.");
        }

        order.setOrderStatus(changeOrderStatusDTO.getOrderStatus());
        orderRepository.save(order);
    }

    // 재고 현황 보여주기
    public List<StockItem> showStockInfo(User user) {
        validateUser(user);
        return stockRepository.findAll();
    }

    // 관리자 권한이 있는 지 확인하기
    public void validateUser(User user) {
        if(user == null) {
            throw new IllegalStateException("로그인 이후에 사용해주세요.");
        }
        if(!user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("관리자 외 접근이 불가능한 페이지입니다.\n직원이시라면 접근 권한 신청 부탁드립니다.");
        }
    }
}
