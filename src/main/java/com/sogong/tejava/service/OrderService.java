package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.*;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.entity.options.OptionsItem;
import com.sogong.tejava.entity.style.StyleItem;
import com.sogong.tejava.repository.*;
import com.sogong.tejava.util.Const;
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
    2. 주문한 내역에서 옵션이나 스타일 수정 (접수 대기 중에서만 가능)
    3. 주문한 내역에서 주문 취소 (접수 대기 중에서만 가능, 부가적 기능)
    4. 주문 내역 보여주기
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

    @Transactional
    // TODO : 주문 이후 메뉴 디테일 수정시, 가격의 일부분 환불 혹은 추가 결제가 필요하기에 주문 이후에 수정할 수 있도록 할 것인지 의논해볼 것!
    // TODO : 금액이 늘어났다면 초과된 가격을 결제하도록 결제화면으로 이동하면 되는 데, 일부 반환을 해야하는 경우, 어떻게 줄 건지! (ex. point or 변경 전후의 가격을 반환하니 판단해서 늘어났으면 결제화면, 줄어들었다면 입금 화면으로 이동할 것)
    // 주문 이후 접수 대기 중인 상태일 때, 메뉴 디테일(옵션/스타일) 수정하기
    public ChangeMenuDetailResponseDTO updateMenuDetail(ChangeMenuDetailDTO changeMenuDetailDTO) {

        User customer = userRepository.findUserById(changeMenuDetailDTO.getUserId());
        userRoleCheck(changeMenuDetailDTO.getUserId());

        Order order = orderRepository.findOrderById(changeMenuDetailDTO.getOrderId());
        double totalPrice = 0.0;
        for (Menu menu : menuRepository.findAllByOrderId(order.getId())) {
            totalPrice += menu.getPrice();
        }

        if (!order.getOrder_status().equals(OrderStatus.pending.toString())) {
            throw new IllegalStateException("주문이 이미 접수되어 수정 불가합니다.");
        }

        ChangeMenuDetailResponseDTO changeMenuDetailResponseDTO = new ChangeMenuDetailResponseDTO();

        // 수정될 메뉴를 가져와 옵션/스타일 초기화하면서 totalPrice 도 차감
        Menu menu = menuRepository.getMenuById(changeMenuDetailDTO.getMenuId()); // 수정할 메뉴를 가져옴

        optionsRepository.deleteAllByMenuId(menu.getId());
        styleRepository.deleteByMenuId(menu.getId());
        menuRepository.save(menu);

        OrderHistory orderHistory = customer.getOrderHistory();

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
            totalPrice += (optionsDTO.getPrice() * optionsDTO.getQuantity() * menu.getQuantity());
        }
        optionsRepository.saveAll(newOptions);

        // 새로운 스타일 생성
        Style style = new Style();
        style.setStyle_nm(changeMenuDetailDTO.getNewStyle().getStyle_nm());
        style.setStyle_pic(changeMenuDetailDTO.getNewStyle().getStyle_pic());
        style.setStyle_config(changeMenuDetailDTO.getNewStyle().getStyle_config());
        style.setPrice(changeMenuDetailDTO.getNewStyle().getPrice());
        style.setMenu(menu);
        totalPrice += (changeMenuDetailDTO.getNewStyle().getPrice() * menu.getQuantity());
        styleRepository.save(style);

        // 새로 생성한 메뉴에 새로운 옵션/스타일 반영
        menu.setOptions(newOptions);
        menu.setStyle(style);
        menuRepository.save(menu);

        stockQuantityCheck(List.of(menu));

        // 반환할 dto 의 정보 세팅
        changeMenuDetailResponseDTO.setPre_price(order.getTotal_price());
        changeMenuDetailResponseDTO.setPost_price(totalPrice);

        // 주문의 수정사항 저장
        order.setTotal_price(totalPrice);
        order.setMenu(menuRepository.findAllByOrderId(order.getId()));
        order.setOrderHistory(orderHistory);

        // db에 갱신
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);

        return changeMenuDetailResponseDTO;
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
    public List<MenuDTO> showOrderHistory(HttpServletRequest request) {

        User user = getUserFromRequest(request);

        OrderHistory orderHistory = orderHistoryRepository.findByUserId(user.getId());

        List<MenuDTO> menuDTOList = new ArrayList<>();
        for (Order order : orderRepository.findAllByOrderHistoryId(orderHistory.getId())) {
            menuDTOList.addAll(order.getMenu().stream().map(MenuDTO::from).collect(Collectors.toList()));
        }

        if (user.getRole().equals(Role.NOT_MEMBER)) {
            return null;
        }

        return menuDTOList;
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

    public User getUserFromRequest(HttpServletRequest request) {
        User loginMember = (User) request.getSession(false).getAttribute(SessionConst.LOGIN_MEMBER);
        User notMember = (User) request.getSession(false).getAttribute(SessionConst.NOT_MEMBER);

        if (loginMember == null) {
            return notMember;
        } else {
            return loginMember;
        }
    }

    public void employeeCheck() {
        if (Const.chef <= 0) {
            throw new IllegalStateException("현재 요리 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        } else if (Const.delivery <= 0) {
            throw new IllegalStateException("현재 배달 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        }

        log.info("요리 가능한 인원 수 : " + Const.chef + "명");
        log.info("배달 가능한 인원 수 : " + Const.delivery + "명");
    }

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