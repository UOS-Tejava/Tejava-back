package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.*;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.options.OptionsItem;
import com.sogong.tejava.entity.style.StyleItem;
import com.sogong.tejava.repository.*;
import com.sogong.tejava.util.OrderDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final MenuItemRepository menuItemRepository;
    private final OptionsItemRepository optionsItemRepository;
    private final StyleItemRepository styleItemRepository;
    double totalPrice = 0.0;

    // 주문하기
    public OrderResultDTO placeOrder(ShoppingCartDTO shoppingCartDTO, OrderDateTime orderDateTime) {

        User customer = userRepository.findUserById(shoppingCartDTO.getUserId());
        userRoleCheck(shoppingCartDTO.getUserId());

        OrderResultDTO orderResultDTO = new OrderResultDTO();
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
        orderResultDTO.setCustomerName(customer.getName());
        orderResultDTO.setCustomerAddress(customer.getAddress());
        orderResultDTO.setOrderDateAndTime(orderDateTime.getDateTime());
        orderResultDTO.setTotalPrice(totalPrice);

        return orderResultDTO;
    }

    // 주문한 내역에서 메뉴의 옵션 수정
    public void updateMenuOptions(ChangeOptionsDTO changeOptionsDTO) {

        User customer = userRepository.findUserById(changeOptionsDTO.getUserId());
        userRoleCheck(changeOptionsDTO.getUserId());

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
        List<Options> newOptions = new ArrayList<>();

        for (OptionsDTO optionsDTO : changeOptionsDTO.getNewOptions()) {
            Options option = new Options();
            option.setOption_nm(optionsDTO.getOption_nm());
            option.setOption_pic(optionsDTO.getOption_pic());
            option.setQuantity(option.getQuantity());
            option.setPrice(option.getPrice());

            newOptions.add(option);
        }

        menu.setOptions(newOptions);

        // 수정사항 저장
        order.getMenu().add(0, menu);
        orderHistory.getOrder().add(0, order);
        order.setOrder_status(OrderStatus.pending);

        // 고객의 주문 내역과 전체 주문 테이블에 객체 저장
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    public void updateMenuStyle(ChangeStyleDTO changeStyleDTO) {

        User customer = userRepository.findUserById(changeStyleDTO.getUserId());
        userRoleCheck(changeStyleDTO.getUserId());

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

        Style style = new Style();
        style.setStyle_nm(changeStyleDTO.getNewStyle().getStyle_nm());
        style.setStyle_pic(changeStyleDTO.getNewStyle().getStyle_pic());
        style.setStyle_config(changeStyleDTO.getNewStyle().getStyle_config());
        style.setPrice(changeStyleDTO.getNewStyle().getPrice());

        menu.setStyle(style);

        // 수정사항 저장
        order.getMenu().add(0, menu);
        orderHistory.getOrder().add(0, order);

        // 고객의 주문 내역과 전체 주문 테이블에 객체 저장
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    // 주문한 내역에서 주문 취소
    public void cancelOrder(CancelOrderDTO cancelOrderDTO) {

        User customer = userRepository.findUserById(cancelOrderDTO.getUserId());
        userRoleCheck(cancelOrderDTO.getUserId());

        OrderHistory orderHistory = customer.getOrderHistory();

        List<Order> orderList = customer.getOrderHistory().getOrder();
        Order order = new Order();
        for (Order order1 : orderList) {
            if (order1.getId().equals(cancelOrderDTO.getOrderId())) {
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
    public List<MenuDTO> showOrderHistory(UserIdDTO userIdDTO) {

        User customer = userRepository.findUserById(userIdDTO.getUserId());
        userRoleCheck(userIdDTO.getUserId());

        OrderHistory orderHistory = customer.getOrderHistory();

        List<MenuDTO> result = new ArrayList<>();
        for (Order order : orderHistory.getOrder()) {
            //result.addAll(order.getMenu().stream().map(MenuDTO::from).collect(Collectors.toList()));
        }
        return result;
    }

    // 고객의 주문 내역 모두 삭제하기
    public void deleteOrderHistory(UserIdDTO userIdDTO) {

        User customer = userRepository.findUserById(userIdDTO.getUserId());
        userRoleCheck(userIdDTO.getUserId());

        OrderHistory orderHistory = customer.getOrderHistory();
        orderHistory.getOrder().clear();

        orderHistoryRepository.save(orderHistory);
    }

    public List<MenuItemDTO> showAllMenus() {
        return menuItemRepository.findAll().stream().map(MenuItemDTO::from).collect(Collectors.toList());
    }

    public List<OptionsDTO> showAllOptions(Long menuId) {
        List<OptionsItem> array = optionsItemRepository.findAll();
        List<OptionsItem> optionsList = new ArrayList<>();

        // 메뉴별로 보여지는 옵션이 다르기에 고유한 아이디를 매개변수로 받아 해당 메뉴의 옵션만 보여주기 위함
        for (OptionsItem optionsItem : array) {
            if (optionsItem.getMenuItem().getId().equals(menuId)) {
                optionsList.add(optionsItem);
            }
        }

        return optionsList.stream().map(OptionsDTO::from).collect(Collectors.toList());
    }

    public List<StyleDTO> showAllStyles(Long menuId) {
        List<StyleItem> styleList = styleItemRepository.findAll();

        // 샴페인 축제 디너의 경우, 심플 디너 스타일을 제공하지 않음!
        if (menuId.equals(4L)) {
            styleList.remove(0);
        }

        return styleList.stream().map(StyleDTO::from).collect(Collectors.toList());
    }

    public void userRoleCheck(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("일반 회원이 사용하실 수 있는 기능입니다.");
        }
    }
}
