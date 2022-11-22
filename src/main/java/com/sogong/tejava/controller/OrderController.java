package com.sogong.tejava.controller;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문한 이후, 주문 내역에서 상태가 접수 대기중이 아닐 경우, 결제 취소하기 -> 주문내역에서도 삭제!
    @DeleteMapping("/order/cancel/orderId/{orderId}")
    @ApiOperation(value = "주문 이후에 결제 취소하기", notes = "주문된 메뉴의 상태가 접수 대기중일 때만 가능하며, 비회원이 아니라면 주문 횟수가 1 줄어듭니다.")
    public ResponseEntity<?> cancelOrder(HttpServletRequest request, @PathVariable Long orderId) {

        orderService.cancelOrder(request, orderId);
        return ResponseEntity.ok().build();
    }

    // 주문하기 : 결제 정보는 프론트에서만 다루고 db에 따로 저장하진 않는 것으로 결정
    @PostMapping("/order/place-order") // TODO : 심플 스타일을 선택하는 경우, 와인이 포함되어 있다면 플라스틱 와인잔이 제공됨을 명시해야 한다 !
    @ApiOperation(value = "주문하기", notes = "회원의 주문 내역과 직원 인터페이스 화면의 주문 목록에도 추가되며, 주문 횟수가 1 늘어납니다.")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderDTO orderDTO) {

        OrderResponseDTO orderResponseDTO = orderService.placeOrder(orderDTO);
        return ResponseEntity.ok().body(orderResponseDTO);
    }

    // 회원의 주문 내역 반환하기
    @GetMapping("/order/history")
    @ApiOperation(value = "주문 내역 보기", notes = "회원의 주문 내역을 반환합니다. 비회원의 경우, 아무것도 반환하지 않습니다.")
    public ResponseEntity<List<OrderHistoryResponseDTO>> showOrderHistory(HttpServletRequest request) {

        return ResponseEntity.ok().body(orderService.showOrderHistory(request));
    }

    @GetMapping("/order/showAllMenus")
    @ApiOperation(value = "모든 메뉴 보기", notes = "리스트 형식으로 반환합니다.")
    public ResponseEntity<List<MenuItemDTO>> showAllMenus() {

        List<MenuItemDTO> menuList = orderService.showAllMenus();
        return ResponseEntity.ok().body(menuList);
    }

    @GetMapping("/order/showAllOptions/menuId/{menuId}")
    @ApiOperation(value = "특정 메뉴의 가능한 옵션 보기", notes = "메뉴별 선택할 수 있는 옵션이 다르며, 리스트 형식으로 반환합니다.")
    public ResponseEntity<List<OptionsDTO>> showAllOptions(@PathVariable Long menuId) {

        List<OptionsDTO> optionsList = orderService.showAllOptions(menuId);
        return ResponseEntity.ok().body(optionsList);
    }

    @GetMapping("/order/showAllStyles/menuId/{menuId}")
    @ApiOperation(value = "특정 메뉴의 가능한 스타일 보기", notes = "메뉴별 선택할 수 있는 스타일이 다르며, 리스트 형식으로 반환합니다.")
    public ResponseEntity<List<StyleDTO>> showAllStyles(@PathVariable Long menuId) {

        List<StyleDTO> styleList = orderService.showAllStyles(menuId);
        return ResponseEntity.ok().body(styleList);
    }
}