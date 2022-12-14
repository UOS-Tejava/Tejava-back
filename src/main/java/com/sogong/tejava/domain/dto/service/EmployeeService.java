package com.sogong.tejava.domain.dto.service;

import com.sogong.tejava.domain.dto.*;
import com.sogong.tejava.domain.dto.repository.*;
import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.OrderStatus;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.Menu;
import com.sogong.tejava.entity.customer.Options;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.validate.config.util.Const;
import com.sogong.tejava.validate.config.util.EmployeeCapacity;
import com.sogong.tejava.validate.config.util.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {
    /*
    1. 들어온 주문 조회
    2. 주문의 상태 변경하기(pending, cooking, delivering, completed)
    3. 재고 현황 보여주기
    4. 재고 수량 수정하기
    (5. 현재 세션에 있는 유저의 권한과 파라미터로 받은 유저의 권한이 동일한 지 확인하기)
    (6. 관리자 권한이 있는 지 확인하기)
    (7. 요청으로부터 회원 객체 반환하기)
     */

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderHistoryRepository orderHistoryRepository;


    @Autowired
    public EmployeeService(StockRepository stockRepository, OrderRepository orderRepository, UserRepository userRepository, MenuRepository menuRepository, OrderHistoryRepository orderHistoryRepository) {
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    // 들어온 모든 주문 조회
    public GetOrderListResponseDTO getOrderList(HttpServletRequest request) {

        User user = getUserFromRequest(request);

        requestCheck(request, user.getId());
        userRoleCheck(user.getId());

        User customer = userRepository.findUserById(user.getId());
        List<Order> orders = orderRepository.findAll();

        List<GetOrderListResponseItemDTO> result = new ArrayList<>();

        for (Order order : orders) {
            GetOrderListResponseItemDTO responseDTO = new GetOrderListResponseItemDTO();

            responseDTO.setOrderId(order.getId());
            responseDTO.setOrderedDate(order.getCreatedDate());
            responseDTO.setCustomerName(orderHistoryRepository.findByOrderId(order.getId()).getUser().getName());
            responseDTO.setCustomerAddress(customer.getAddress());
            responseDTO.setMenuDTOList(menuRepository.findAllByOrderId(order.getId()).stream().map(MenuDTO::from).collect(Collectors.toList()));
            responseDTO.setTotalPrice(order.getTotal_price());
            responseDTO.setReq_orderDateTime(order.getReq_orderDateTime());
            responseDTO.setReq_orderDateTime(order.getReq_orderDateTime());
            responseDTO.setOrderStatus(order.getOrder_status());

            result.add(responseDTO);
        }

        GetOrderListResponseDTO responseDTO = new GetOrderListResponseDTO();
        responseDTO.setOrderList(result);
        responseDTO.setChef(EmployeeCapacity.getChef());
        responseDTO.setDelivery(EmployeeCapacity.getDelivery());

        return responseDTO;
    }

    // 주문 상태 변경하기
    public void updateOrderStatus(HttpServletRequest request, ChangeOrderStatusDTO changeOrderStatusDTO) {

        User user = getUserFromRequest(request);
        requestCheck(request, changeOrderStatusDTO.getEmployeeId());
        userRoleCheck(user.getId());

        Order order = orderRepository.findOrderById(changeOrderStatusDTO.getOrderId());

        if (order == null) {
            throw new IllegalStateException("선택하신 주문이 존재하지 않습니다.");
        }

        order.setOrder_status(changeOrderStatusDTO.getOrderStatus());

        // 요리 중으로 상태가 바뀌는 경우, 재고 현황에 반영할 것
        if (changeOrderStatusDTO.getOrderStatus().equals(OrderStatus.cooking.toString())) {

            employeeCheck();

            List<StockItem> stockItems = stockRepository.findAll();

            StockItem wine = stockItems.get(0);
            StockItem coffee = stockItems.get(2);
            StockItem cheese = stockItems.get(4);
            StockItem salad = stockItems.get(3);
            StockItem bread = stockItems.get(5);
            StockItem champagne = stockItems.get(1);
            StockItem baguette = stockItems.get(6);

            // 재고 현황에 반영
            for (Menu menu : order.getMenu()) {
                for (Options option : menu.getOptions()) {
                    switch (option.getOption_nm()) {
                        case "와인 한 잔":
                            if (wine.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("와인의 재고가 부족합니다.");
                            }
                            wine.setQuantity(wine.getQuantity() - option.getQuantity());
                            break;
                        case "커피 한 잔":
                            if (coffee.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("커피의 재고가 부족합니다.");
                            }
                            coffee.setQuantity(coffee.getQuantity() - option.getQuantity());
                            break;
                        case "치즈":
                            if (cheese.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("치즈의 재고가 부족합니다.");
                            }
                            cheese.setQuantity(cheese.getQuantity() - option.getQuantity());
                            break;
                        case "샐러드":
                            if (salad.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("샐러드의 재고가 부족합니다.");
                            }
                            salad.setQuantity(salad.getQuantity() - option.getQuantity());
                            break;
                        case "빵":
                            if (bread.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("빵의 재고가 부족합니다.");
                            }
                            bread.setQuantity(bread.getQuantity() - Const.BREAD_OPTION_QUANTITY * option.getQuantity());
                            break;
                        case "바게트 빵":
                            if (baguette.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("바게트 빵의 재고가 부족합니다.");
                            }
                            bread.setQuantity(baguette.getQuantity() - Const.BAGUETTE_OPTION_QUANTITY * option.getQuantity());
                            break;
                        case "샴페인 한 병":
                            if (champagne.getQuantity() < option.getQuantity()) {
                                throw new IllegalStateException("샴페인의 재고가 부족합니다.");
                            }
                            champagne.setQuantity(champagne.getQuantity() - option.getQuantity());
                            break;
                    }
                }
            }

            stockRepository.save(wine);
            stockRepository.save(coffee);
            stockRepository.save(cheese);
            stockRepository.save(salad);
            stockRepository.save(bread);
            stockRepository.save(champagne);
            stockRepository.save(baguette);

            if (EmployeeCapacity.getChef() >= 1) {
                EmployeeCapacity.decreaseChef();
            } else {
                throw new IllegalStateException("가용한 요리 담당 직원이 없습니다.");
            }

        } else if (changeOrderStatusDTO.getOrderStatus().equals(OrderStatus.delivering.toString())) {

            employeeCheck();

            if (EmployeeCapacity.getChef() == 5) {
                throw new IllegalStateException("요리 담당 직원은 5명보다 많아질 수 없습니다.");
            } else if (EmployeeCapacity.getDelivery() == 0) {
                throw new IllegalStateException("가용한 배달 담당 직원이 없습니다.");
            }

            EmployeeCapacity.increaseChef();
            EmployeeCapacity.decreaseDelivery();
        } else if (changeOrderStatusDTO.getOrderStatus().equals(OrderStatus.completed.toString())) {
            if (EmployeeCapacity.getDelivery() == 5) {
                throw new IllegalStateException("배달 담당 직원은 5명보다 많아질 수 없습니다.");
            }
            EmployeeCapacity.increaseDelivery();
        }

        log.info("요리 가능한 인원 수 : " + EmployeeCapacity.getChef() + "명");
        log.info("배달 가능한 인원 수 : " + EmployeeCapacity.getDelivery() + "명");

        orderRepository.save(order);
    }

    // 재고 현황 보여주기
    public List<StockItemDTO> showStockInfo(HttpServletRequest request) {

        User user = getUserFromRequest(request);

        requestCheck(request, user.getId());
        userRoleCheck(user.getId());

        return stockRepository.findAll().stream().map(StockItemDTO::from).collect(Collectors.toList());
    }

    // 재고 수량 수정하기
    public void changeStockInfo(HttpServletRequest request, ChangeStockInfoDTO changeStockInfoDTO) {

        User user = getUserFromRequest(request);
        requestCheck(request, changeStockInfoDTO.getEmployeeId());
        userRoleCheck(user.getId());

        StockItem stockItem = stockRepository.getById(changeStockInfoDTO.getStockItemId());
        stockItem.setQuantity(changeStockInfoDTO.getQuantity());
        stockRepository.save(stockItem);
    }

    // 일반 유저가 직원의 id 알아서 악의적으로 요청하는 경우를 체크
    public void requestCheck(HttpServletRequest request, Long userId) {
        if (((User) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER)).getRole().equals(Role.USER) || userRepository.findUserById(userId).getRole().equals(Role.USER)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
    }

    // 관리자 권한이 있는 지 확인하기
    public void userRoleCheck(Long userId) {

        User user = userRepository.findUserById(userId);

        if (!user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("관리자 외 접근이 불가능한 페이지입니다.\n직원이시라면 접근 권한 신청 부탁드립니다.");
        }
    }

    // 요청으로부터 회원 객체 반환하기
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
        if (EmployeeCapacity.getChef() <= 0) { // cooking 으로 변경하고자 할 시
            throw new IllegalStateException("현재 요리 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        } else if (EmployeeCapacity.getDelivery() <= 0) { // delivering 으로 변경하고자 할 시
            throw new IllegalStateException("현재 배달 가능한 인원이 없어 잠시만 기다려주시면 감사하겠습니다.");
        }

        log.info("요리 가능한 인원 수 : " + EmployeeCapacity.getChef() + "명");
        log.info("배달 가능한 인원 수 : " + EmployeeCapacity.getDelivery() + "명");
    }
}