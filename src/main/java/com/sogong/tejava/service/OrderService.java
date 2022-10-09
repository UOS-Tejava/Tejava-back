package com.sogong.tejava.service;

import com.sogong.tejava.dto.ChangeOptionsDTO;
import com.sogong.tejava.dto.ChangeStyleDTO;
import com.sogong.tejava.dto.OrderResponseDTO;
import com.sogong.tejava.dto.ShoppingCartDTO;
import com.sogong.tejava.entity.*;
import com.sogong.tejava.entity.customer.OrderHistory;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.repository.*;
import com.sogong.tejava.util.OrderDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    /*
    1. 주문하기
    2. 주문한 내역에서 옵션이나 스타일 수정 (접수 대기 중에서만 가능)
    3. 주문한 내역에서 주문 취소 (접수 대기 중에서만 가능, 부가적 기능)
    4. 주문 내역 보여주기
    5. 주문 내역 삭제하기 (부가적 기능)
     */

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final MenuRepository menuRepository;
    private final OptionsRepository optionsRepository;
    private final StyleRepository styleRepository;
    double totalPrice = 0.0;

    // 주문하기
    public OrderResponseDTO placeOrder(User customer, ShoppingCartDTO shoppingCartDTO, OrderDateTime orderDateTime) {

        validateUser(customer);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        List<Menu> menuList = shoppingCartDTO.getShoppingCart().getMenu();
        Order order = new Order();
        OrderHistory orderHistory = customer.getOrderHistory();

        // 총 가격 계산
        for (Menu menu : menuList) {
            for (Options option : menu.getOptions()) {
                totalPrice += option.getPrice();
            }
            totalPrice += (menu.getPrice() + menu.getStyle().getPrice());
        }

        if (customer.getOrder_cnt() >= 5) {
            totalPrice = totalPrice * 0.9;
        }

        // 주문 테이블에 order 객체 저장
        order.setTotal_price(totalPrice);
        order.setMenu(menuList);
        orderRepository.save(order);

        // 고객의 주문 내역에 추가
        orderHistory.getOrder().add(0, order);
        orderHistoryRepository.save(orderHistory);

        // 주문 횟수 1 증가하며 장바구니 초기화
        customer.setOrder_cnt(customer.getOrder_cnt() + 1);
        customer.setShoppingCart(null);
        userRepository.save(customer);

        // 반환할 객체의 정보 setting
        orderResponseDTO.setCustomerName(customer.getName());
        orderResponseDTO.setCustomerAddress(customer.getAddress());
        orderResponseDTO.setOrderDateAndTime(orderDateTime.getDateTime());
        orderResponseDTO.setTotalPrice(totalPrice);

        return orderResponseDTO;
    }

    // 주문한 내역에서 메뉴의 옵션 수정
    public void updateMenuOptions(User customer, ChangeOptionsDTO changeOptionsDTO) {

        validateUser(customer);

        OrderHistory orderHistory = customer.getOrderHistory();

        List<Order> orderList = customer.getOrderHistory().getOrder();
        Order order = new Order();
        for (Order order1 : orderList) {
            if (order1.getId().equals(changeOptionsDTO.getOrderId())) {
                order = order1;
            }
        }

        if (!order.getOrder_status().equals(OrderStatus.pending)) {
            throw new IllegalStateException("주문이 이미 접수되어 수정 불가합니다.");
        }

        // 메뉴의 옵션 수정
        List<Menu> menuList = order.getMenu();
        Menu menu = new Menu();
        for (Menu menu1 : menuList) {
            if (menu1.getId().equals(changeOptionsDTO.getMenuId())) {
                menu = menu1;
                menuList.remove(menu1);
            }
        }
        menu.setOptions(changeOptionsDTO.getNewOptions());

        // 수정사항 저장
        order.getMenu().add(0, menu);
        orderHistory.getOrder().add(0, order);
        order.setOrder_status(OrderStatus.pending);

        // 고객의 주문 내역과 전체 주문 테이블에 객체 저장
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    public void updateMenuStyle(User customer, ChangeStyleDTO changeStyleDTO) {

        validateUser(customer);

        OrderHistory orderHistory = customer.getOrderHistory();

        List<Order> orderList = customer.getOrderHistory().getOrder();
        Order order = new Order();
        for (Order order1 : orderList) {
            if (order1.getId().equals(changeStyleDTO.getOrderId())) {
                order = order1;
            }
        }

        if (!order.getOrder_status().equals(OrderStatus.pending)) {
            throw new IllegalStateException("주문이 이미 접수되어 수정 불가합니다.");
        }

        // 메뉴의 옵션 수정
        List<Menu> menuList = order.getMenu();
        Menu menu = new Menu();
        for (Menu menu1 : menuList) {
            if (menu1.getId().equals(changeStyleDTO.getMenuId())) {
                menu = menu1;
                menuList.remove(menu1);
            }
        }
        menu.setStyle(changeStyleDTO.getNewStyle());

        // 수정사항 저장
        order.getMenu().add(0, menu);
        orderHistory.getOrder().add(0, order);

        // 고객의 주문 내역과 전체 주문 테이블에 객체 저장
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    // 주문한 내역에서 주문 취소
    public void cancelOrder(User customer, Long orderId) {

        validateUser(customer);

        OrderHistory orderHistory = customer.getOrderHistory();

        List<Order> orderList = customer.getOrderHistory().getOrder();
        Order order = new Order();
        for (Order order1 : orderList) {
            if (order1.getId().equals(orderId)) {
                order = order1;
            }
        }

        if (!order.getOrder_status().equals(OrderStatus.pending)) {
            throw new IllegalStateException("주문이 이미 접수되어 취소 불가합니다.");
        }

        // // 고객의 주문 내역과 전체 주문 테이블에서 삭제
        orderHistory.getOrder().remove(order);
        orderHistoryRepository.save(orderHistory);
        orderRepository.delete(order);

        // 주문 횟수 1 감소
        customer.setOrder_cnt(customer.getOrder_cnt() - 1);
        userRepository.save(customer);
    }

    // 고객의 주문 내역 보여주기
    public List<Order> showOrderHistory(User customer) {

        if (customer == null) {
            throw new IllegalStateException("로그인 이후에 사용해주세요.");
        }

        OrderHistory orderHistory = customer.getOrderHistory();
        return orderHistory.getOrder();
    }

    // 고객의 주문 내역 모두 삭제하기
    public void deleteOrderHistory(User customer) {

        if (customer == null) {
            throw new IllegalStateException("로그인 이후에 사용해주세요.");
        }

        validateUser(customer);

        OrderHistory orderHistory = customer.getOrderHistory();
        orderHistory.getOrder().clear();

        orderHistoryRepository.save(orderHistory);
    }

    public List<Menu> showAllMenus() {
        return menuRepository.findAll();
    }

    public List<Options> showAllOptions(Long menuId) {
        List<Options> array = optionsRepository.findAll();
        List<Options> optionsList = new ArrayList<>();

        // 메뉴별로 보여지는 옵션이 다르기에 고유한 아이디를 매개변수로 받아 해당 메뉴의 옵션만 보여주기 위함
        for (Options option : array) {
            if (option.getMenu().getId().equals(menuId)) {
                optionsList.add(option);
            }
        }

        return optionsList;
    }

    public List<Style> showAllStyles(Long menuId) {
        List<Style> array = styleRepository.findAll();
        List<Style> styleList = new ArrayList<>();

        // 메뉴별로 보여지는 스타일이 다르기에 고유한 아이디를 매개변수로 받아 해당 메뉴의 스타일만 보여주기 위함
        for (Style style : array) {
            if (style.getMenu().getId().equals(menuId)) {
                styleList.add(style);
            }
        }

        return styleList;
    }

    public void validateUser(User user) {
        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("일반 회원이 사용하실 수 있는 기능입니다.");
        }
    }
}
