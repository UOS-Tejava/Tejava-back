package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.*;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.options.OptionsItem;
import com.sogong.tejava.entity.style.StyleItem;
import com.sogong.tejava.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final StyleRepository styleRepository;
    private final OptionsRepository optionsRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OptionsItemRepository optionsItemRepository;
    private final StyleItemRepository styleItemRepository;
    double totalPrice = 0.0;

    // 주문하기
    @Transactional
    public OrderResultDTO placeOrder(OrderDTO orderDTO) { // TODO: 프론트에서 total price 계산할 건지 확인할 것!

        User customer = userRepository.findUserById(orderDTO.getUserId());
        userRoleCheck(orderDTO.getUserId());

        OrderResultDTO orderResultDTO = new OrderResultDTO();
        List<MenuDTO> menuDTOList = orderDTO.getShoppingCartItems();
        List<Menu> menuList = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();

        Order order = new Order();
        orderRepository.save(order);
        OrderHistory orderHistory = orderHistoryRepository.findByUserId(orderDTO.getUserId());

        // style, option, menu 저장 및 총 가격 계산
        for (MenuDTO menuDTO : menuDTOList) {
            Menu menu = new Menu();
            menuRepository.save(menu);
            Style style = new Style();

            style.setStyle_nm(menuDTO.getStyle().getStyle_nm());
            style.setStyle_config(menuDTO.getStyle().getStyle_config());
            style.setStyle_pic(menuDTO.getStyle().getStyle_pic());
            style.setMenu(menu);

            styleRepository.save(style);

            totalPrice += (menuDTO.getPrice() + menuDTO.getStyle().getPrice());

            for (OptionsDTO optionsDTO : menuDTO.getOptions()) {
                totalPrice += (optionsDTO.getPrice() * optionsDTO.getQuantity());

                Options option = new Options();
                option.setOption_nm(optionsDTO.getOption_nm());
                option.setOption_pic(optionsDTO.getOption_pic());
                option.setPrice(optionsDTO.getPrice());
                option.setQuantity(optionsDTO.getQuantity());
                option.setMenu(menu);

                optionsRepository.save(option);
            }

            menu.setMenu_nm(menuDTO.getMenu_nm());
            menu.setMenu_pic(menuDTO.getMenu_pic());
            menu.setMenu_config(menuDTO.getMenu_config());
            menu.setQuantity(menuDTO.getQuantity());
            menu.setPrice(menuDTO.getPrice());
            menu.setOrder(order);

            menuRepository.save(menu);
        }

        if (customer.getOrder_cnt() >= 5) {
            totalPrice = totalPrice * 0.9;
        }

        // 주문 테이블에 order 객체 저장
        order.setTotal_price(totalPrice);
        order.setMenu(menuList);
        order.setOrderHistory(orderHistory);
        order.setOrder_status(OrderStatus.pending.toString());
        orderRepository.save(order);

        // 고객의 주문 내역에 추가
        if (orderRepository.existsByOrderHistoryId(orderHistory.getId())) {
            orderList = orderRepository.findAllByOrderHistoryId(orderHistory.getId());
        }

        orderList.add(order);
        orderHistory.setOrder(orderList);
        orderHistoryRepository.save(orderHistory);
        customer.setOrderHistory(orderHistory);

        // 주문 횟수 1 증가하며 장바구니 초기화
        customer.setOrder_cnt(customer.getOrder_cnt() + 1);
        menuRepository.deleteAllByShoppingCartId(shoppingCartRepository.findByUserId(customer.getId()).getId());
        userRepository.save(customer);

        // 장바구니 빈 거 확인
        log.info("장바구니 초기화 확인 : " + menuRepository.findAllByShoppingCartId(shoppingCartRepository.findByUserId(customer.getId()).getId()));

        // 반환할 객체의 정보 setting
        orderResultDTO.setCustomerName(customer.getName());
        orderResultDTO.setCustomerAddress(customer.getAddress());
        orderResultDTO.setOrderDateTime(orderDTO.getOrderDateTime());
        orderResultDTO.setTotalPrice(totalPrice);

        totalPrice = 0.0;

        return orderResultDTO;
    }

    @Transactional // TODO : 주문 이후 메뉴 디테일 수정시, 가격의 일부분 환불 혹은 추가 결제가 필요하기에 주문 이후에 수정할 수 있도록 할 것인지 의논해볼 것!
    // TODO : 금액이 늘어났다면 초과된 가격을 결제하도록 결제화면으로 이동하면 되는 데, 일부 반환을 해야하는 경우, 어떠한 경로로 줄 건지! (ex. point) -> 이렇게 되면 결제 시, 포인트를 사용 가능하게 하면 됨
    // 주문 이후 접수 대기 중인 상태일 때, 메뉴 디테일(옵션/스타일) 수정하기
    public void updateMenuDetail(ChangeMenuDetailDTO changeMenuDetailDTO) {

        User customer = userRepository.findUserById(changeMenuDetailDTO.getUserId());
        userRoleCheck(changeMenuDetailDTO.getUserId());

        OrderHistory orderHistory = customer.getOrderHistory();

        Order order = orderRepository.getById(changeMenuDetailDTO.getOrderId());

        if (!order.getOrder_status().equals(OrderStatus.pending.toString())) {
            throw new IllegalStateException("주문이 이미 접수되어 수정 불가합니다.");
        }

        // 새로운 옵션/스타일이 적용된 메뉴를 주문 리스트에 갱신한다
        Menu menuTmp = menuRepository.getMenuById(changeMenuDetailDTO.getMenuId());
        log.info("수정할 메뉴 : " + menuTmp);

        Menu menu = new Menu();
        menu.setMenu_nm(menuTmp.getMenu_nm());
        menu.setMenu_config(menuTmp.getMenu_config());
        menu.setMenu_pic(menuTmp.getMenu_pic());
        menu.setShoppingCart(menuTmp.getShoppingCart());
        menu.setQuantity(menuTmp.getQuantity());
        menu.setPrice(menuTmp.getPrice());
        menu.setOrder(menuTmp.getOrder());

        menuRepository.save(menu);

        // 새로운 옵션 리스트 생성
        List<Options> newOptions = new ArrayList<>();

        for (OptionsDTO optionsDTO : changeMenuDetailDTO.getNewOptions()) {
            Options option = new Options();
            option.setOption_nm(optionsDTO.getOption_nm());
            option.setOption_pic(optionsDTO.getOption_pic());
            option.setQuantity(optionsDTO.getQuantity());
            option.setPrice(optionsDTO.getPrice());
            option.setMenu(menu);

            newOptions.add(option);
        }

        optionsRepository.saveAll(newOptions);

        // 새로운 스타일 생성
        Style style = new Style();
        style.setStyle_nm(changeMenuDetailDTO.getNewStyle().getStyle_nm());
        style.setStyle_pic(changeMenuDetailDTO.getNewStyle().getStyle_pic());
        style.setStyle_config(changeMenuDetailDTO.getNewStyle().getStyle_config());
        style.setPrice(changeMenuDetailDTO.getNewStyle().getPrice());
        style.setMenu(menu);
        styleRepository.save(style);

        // 새로 생성한 메뉴에 새로운 옵션/스타일 반영
        menu.setOptions(newOptions);
        menu.setStyle(style);

        // 주문의 수정사항 저장
        order.getMenu().add(0, menu);
        order.setMenu(menuRepository.findAllByOrderId(order.getId()));
        order.setOrderHistory(orderHistory);

        // db에 갱신
        menuRepository.save(menu);
        menuRepository.delete(menuTmp);
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    // 주문한 내역에서 주문 취소
    @Transactional
    public void cancelOrder(CancelOrderDTO cancelOrderDTO) {

        User customer = userRepository.findUserById(cancelOrderDTO.getUserId());
        userRoleCheck(cancelOrderDTO.getUserId());

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(customer.getId());

        Order order = orderRepository.findOrderById(cancelOrderDTO.getOrderId());

        if (!order.getOrder_status().equals(OrderStatus.pending.toString())) {
            throw new IllegalStateException("주문이 이미 접수되어 취소 불가합니다.");
        }

        // 주문 삭제
        orderRepository.delete(order);
        orderHistory.setOrder(orderRepository.findAllByOrderHistoryId(orderHistory.getId()));
        orderHistoryRepository.save(orderHistory);

        // 주문 횟수 1 감소
        customer.setOrder_cnt(customer.getOrder_cnt() - 1);
        userRepository.save(customer);
    }

    // 고객의 주문 내역 보여주기
    public List<MenuDTO> showOrderHistory(UserIdDTO userIdDTO) {

        User customer = userRepository.findUserById(userIdDTO.getUserId());
        userRoleCheck(userIdDTO.getUserId());

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(customer.getId());

        List<MenuDTO> result = new ArrayList<>();
        for (Order order : orderRepository.findAllByOrderHistoryId(orderHistory.getId())) {
            result.addAll(order.getMenu().stream().map(MenuDTO::from).collect(Collectors.toList()));
        }
        return result;
    }

    // 고객의 주문 내역 모두 삭제하기
    @Transactional
    public void deleteOrderHistory(UserIdDTO userIdDTO) {

        User customer = userRepository.findUserById(userIdDTO.getUserId());
        userRoleCheck(userIdDTO.getUserId());

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(userIdDTO.getUserId());
        List<Order> orderList = orderRepository.findAllByOrderHistoryId(orderHistory.getId());
        orderRepository.deleteAll(orderList);

        orderHistoryRepository.save(orderHistory);
        customer.setOrderHistory(orderHistory);
        userRepository.save(customer);
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
