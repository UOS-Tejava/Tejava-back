package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.*;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.entity.options.OptionsItem;
import com.sogong.tejava.entity.style.StyleItem;
import com.sogong.tejava.repository.*;
import com.sogong.tejava.util.EmployeeCapacity;
import com.sogong.tejava.util.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    /*
    1. 주문하기
    2. 주문한 내역에서 주문 취소 (접수 대기 중에서만 가능, 부가적 기능)
    3. 주문 내역 보여주기
    4. 모든 메뉴 보여주기
    5. 특정 메뉴의 적용 가능한 옵션 보여주기
    6. 특정 메뉴의 적용 가능한 스타일 보여주기
    (7. 권한 체크 (주문은 일반 회원만 사용할 수 있는 기능))
    (8. 요청으로부터 회원 객체 가져오기)
    (9. 요리/배달 인원 수 체크)
    (10. 재고 현황 체크)
     */

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final MenuRepository menuRepository;
    private final OptionsRepository optionsRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OptionsItemRepository optionsItemRepository;
    private final StyleItemRepository styleItemRepository;
    private final StockRepository stockRepository;

    // 주문하기
    @Transactional
    public OrderResponseDTO placeOrder(OrderDTO orderDTO) {

        employeeCheck();

        User customer = userRepository.findUserById(orderDTO.getUserId());
        userRoleCheck(orderDTO.getUserId());

        Order order = new Order();
        orderRepository.save(order);

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(orderDTO.getUserId());

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(customer.getId());

        List<Menu> menuList = menuRepository.findAllByShoppingCartId(shoppingCart.getId());

        stockQuantityCheck(menuList);

        // 주문 테이블에 order 객체 저장
        order.setTotal_price(orderDTO.getTotal_price());
        order.setMenu(menuList);
        order.setOrderHistory(orderHistory);
        order.setOrder_status(OrderStatus.pending.toString());
        order.setReq_orderDateTime(orderDTO.getReq_orderDateTime());
        orderRepository.save(order);

        // 고객의 주문 내역에 추가
        orderHistory.getOrder().add(order);
        orderHistoryRepository.save(orderHistory);
        customer.setOrderHistory(orderHistory);

        // 최신 정보로 고객의 정보 갱신 (이름, 주소)
        customer.setName(orderDTO.getCustomerName());
        customer.setAddress(orderDTO.getCustomerAddress());

        // 주문 횟수 1 증가하며 menu 에 orderId를 세팅하고, shoppingCartId는 null 로 초기화
        customer.setOrder_cnt(customer.getOrder_cnt() + 1);

        for (Menu menu : menuRepository.findAllByOrderId(order.getId())) {
            menu.setOrder(order);
            menu.setShoppingCart(null);
            menuRepository.save(menu);
        }
        userRepository.save(customer);

        // 장바구니 빈 거 확인
        log.info("장바구니 초기화 확인 : " + menuRepository.findAllByShoppingCartId(shoppingCartRepository.findByUserId(customer.getId()).getId()));

        // 반환할 객체의 정보 setting
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setUserId(customer.getId());
        orderResponseDTO.setOrderId(order.getId());
        orderResponseDTO.setCustomerName(orderDTO.getCustomerName());
        orderResponseDTO.setCustomerAddress(orderDTO.getCustomerAddress());
        orderResponseDTO.setOrderDateTime(order.getCreatedDate());
        orderResponseDTO.setReq_orderDateTime(orderDTO.getReq_orderDateTime());
        orderResponseDTO.setTotalPrice(orderDTO.getTotal_price());

        return orderResponseDTO;
    }

    // 주문한 내역에서 주문 취소
    @Transactional
    public void cancelOrder(HttpServletRequest request, Long orderId) {

        User user = getUserFromRequest(request);
        userRoleCheck(user.getId());

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(user.getId());

        Order order = orderRepository.findOrderById(orderId);

        if (!order.getOrder_status().equals(OrderStatus.pending.toString())) {
            throw new IllegalStateException("주문이 이미 접수되어 취소 불가합니다.");
        }

        // 주문 삭제
        orderRepository.delete(order);
        orderHistory.setOrder(orderRepository.findAllByOrderHistoryId(orderHistory.getId()));
        orderHistoryRepository.save(orderHistory);

        // 비회원이 아니라면 주문 횟수 1 감소
        if (!user.getRole().equals(Role.NOT_MEMBER)) {
            user.setOrder_cnt(user.getOrder_cnt() - 1);
            userRepository.save(user);
        }
    }

    // 고객의 주문 내역 보여주기
    public List<OrderHistoryResponseDTO> showOrderHistory(HttpServletRequest request) {

        User user = getUserFromRequest(request);

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(user.getId());

        List<OrderHistoryResponseDTO> response = new ArrayList<>();
        for (Order order : orderRepository.findAllByOrderHistoryId(orderHistory.getId())) {
            List<OrderHistoryResponseDTO> menus = order.getMenu().stream().map(OrderHistoryResponseDTO::from).collect(Collectors.toList());
            for (OrderHistoryResponseDTO menu : menus) {
                // 주문 시, 주문자의 이름과 주소가 db에 즉각 반영되는 것을 확인하였으나, 여기서 user.getName(), user.getCustomerName()으로 가져올 경우, 세션을 통해 가져와서 db 반영 전 내용을 가져오는 것을 확인
                // 이에 따라 db에 접근해서 직접가져오도록 함
                menu.setCustomerName(userRepository.findUserByOrderHistoryId(orderHistory.getId()).getName());
                menu.setCustomerAddress(userRepository.findUserByOrderHistoryId(orderHistory.getId()).getAddress());
                menu.setOrderDateTime(orderRepository.findOrderByMenuId(menu.getMenuId()).getCreatedDate());
                menu.setReq_orderDateTime(orderRepository.findOrderByMenuId(menu.getMenuId()).getReq_orderDateTime());

                response.add(menu);
            }
        }

        if (user.getRole().equals(Role.NOT_MEMBER)) {
            return null;
        }

        return response;
    }

    // 모든 메뉴 보여주기
    public List<MenuItemDTO> showAllMenus() {
        return menuItemRepository.findAll().stream().map(MenuItemDTO::from).collect(Collectors.toList());
    }

    // 특정 메뉴의 적용 가능한 옵션 보여주기
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

    // 특정 메뉴의 적용 가능한 스타일 보여주기
    public List<StyleDTO> showAllStyles(Long menuId) {
        List<StyleItem> styleList = styleItemRepository.findAll();

        // 샴페인 축제 디너의 경우, 심플 디너 스타일을 제공하지 않음!
        if (menuId.equals(4L)) {
            styleList.remove(0);
        }

        return styleList.stream().map(StyleDTO::from).collect(Collectors.toList());
    }

    // 권한 체크 (주문은 일반 회원만 사용할 수 있는 기능)
    public void userRoleCheck(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("일반 회원이 사용하실 수 있는 기능입니다.");
        }
    }

    // 요청으로부터 회원 객체 가져오기
    public User getUserFromRequest(HttpServletRequest request) {
        User loginMember = (User) request.getSession(false).getAttribute(SessionConst.LOGIN_MEMBER);
        User notMember = (User) request.getSession(false).getAttribute(SessionConst.NOT_MEMBER);

        if (loginMember == null) {
            return notMember;
        } else {
            return loginMember;
        }
    }

    // 요리/배달 인원 수 체크
    public void employeeCheck() {
        if (EmployeeCapacity.getChef() <= 0) {
            throw new IllegalStateException("현재 요리 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        } else if (EmployeeCapacity.getDelivery() <= 0) {
            throw new IllegalStateException("현재 배달 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        }

        log.info("요리 가능한 인원 수 : " + EmployeeCapacity.getChef() + "명");
        log.info("배달 가능한 인원 수 : " + EmployeeCapacity.getDelivery() + "명");
    }

    // 재고 현황 체크
    private void stockQuantityCheck(List<Menu> menuList) {

        List<StockItem> stockItems = stockRepository.findAll();

        StockItem wine = stockItems.get(0);
        StockItem coffee = stockItems.get(2);
        StockItem cheese = stockItems.get(4);
        StockItem salad = stockItems.get(3);
        StockItem bread = stockItems.get(5);
        StockItem champagne = stockItems.get(1);

        for (Menu menu : menuList) {
            for (Options option : menu.getOptions()) {
                switch (option.getOption_nm()) {
                    case "와인 한 잔":
                        if (option.getQuantity() > wine.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("와인 재고가 부족합니다.");
                        }
                    case "커피 한 잔":
                        if (option.getQuantity() > coffee.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("커피 재고가 부족합니다.");
                        }
                    case "치즈":
                        if (option.getQuantity() > cheese.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("치즈 재고가 부족합니다.");
                        }
                    case "샐러드":
                        if (option.getQuantity() > salad.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("샐러드 재고가 부족합니다.");
                        }
                    case "빵":
                    case "바게트 빵":
                        if (option.getQuantity() > bread.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("빵 재고가 부족합니다.");
                        }
                    case "샴페인 한 병":
                        if (option.getQuantity() > champagne.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("샴페인 재고가 부족합니다.");
                        }
                }
            }
        }
    }
}