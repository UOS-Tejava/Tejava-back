package com.sogong.tejava.controller;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.customer.Menu;
import com.sogong.tejava.entity.menu.MenuItem;
import com.sogong.tejava.entity.options.OptionsItem;
import com.sogong.tejava.entity.style.StyleItem;
import com.sogong.tejava.service.OrderService;
import com.sogong.tejava.util.OrderDateTime;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문한 이후, 주문 내역에서 상태가 접수 대기중이 아닐 경우, 메뉴의 옵션 수정하기
    @PatchMapping("/order/update/menu-options")
    @ApiOperation(value = "주문 이후에 메뉴의 옵션 수정", notes = "주문된 메뉴의 상태가 접수 대기중일 때만 가능합니다.")
    public ResponseEntity<?> updateMenuOptions(@RequestBody ChangeOptionsDTO changeOptionsDTO) {

        orderService.updateMenuOptions(changeOptionsDTO);
        return ResponseEntity.ok().build();
    }

    // 주문한 이후, 주문 내역에서 상태가 접수 대기중이 아닐 경우, 메뉴의 스타일 수정하기
    @PatchMapping("/order/update/menu-style")
    @ApiOperation(value = "주문 이후에 메뉴의 스타일 수정", notes = "주문된 메뉴의 상태가 접수 대기중일 때만 가능합니다.")
    public ResponseEntity<?> updateMenuStyle(@RequestBody ChangeStyleDTO changeStyleDTO) {

        orderService.updateMenuStyle(changeStyleDTO);
        return ResponseEntity.ok().build();
    }

    // 주문한 이후, 주문 내역에서 상태가 접수 대기중이 아닐 경우, 결제 취소하기 -> 주문내역에서도 삭제!
    @DeleteMapping("/order/delete/{orderId}")
    @ApiOperation(value = "주문 이후에 결제 취소하기", notes = "주문된 메뉴의 상태가 접수 대기중일 때만 가능하며, 주문 횟수가 1 줄어듭니다.")
    public ResponseEntity<?> cancelOrder(@RequestBody CancelOrderDTO cancelOrderDTO) {

        orderService.cancelOrder(cancelOrderDTO);
        return ResponseEntity.ok().build();
    }

    // 주문하기 : 결제 정보는 프론트에서만 다루고 db에 따로 저장하진 않는 것으로 결정
    @PostMapping("/order/placeOrder")
    @ApiOperation(value = "주문하기", notes = "회원의 주문 내역과 직원 인터페이스 화면의 주문 목록에도 추가되며, 주문 횟수가 1 늘어납니다.")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody ShoppingCartDTO shoppingCartDTO, @RequestBody OrderDateTime orderDateTime) {

        OrderResponseDTO orderResponseDTO = orderService.placeOrder(shoppingCartDTO, orderDateTime);
        return ResponseEntity.ok().body(orderResponseDTO);
    }

    // 회원의 주문 내역 반환하기
    @PostMapping("/order/history")
    @ApiOperation(value = "주문 내역 보여주기", notes = "회원의 주문 내역을 반환합니다.")
    public ResponseEntity<List<Menu>> showOrderHistory(@RequestBody UserIdDTO userIdDTO) {

        return ResponseEntity.ok().body(orderService.showOrderHistory(userIdDTO));
    }

    @DeleteMapping("/order/history")
    @ApiOperation(value = "주문 내역 모두 삭제하기", notes = "회원의 주문 내역을 모두 삭제합니다.")
    public ResponseEntity<?> deleteOrderHistory(@RequestBody UserIdDTO userIdDTO) {

        orderService.deleteOrderHistory(userIdDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/showAllMenus")
    @ApiOperation(value = "모든 메뉴 보여주기", notes = "리스트 형식으로 반환합니다.")
    public ResponseEntity<List<MenuItem>> showAllMenus() {

        List<MenuItem> menuList = orderService.showAllMenus();
        return ResponseEntity.ok().body(menuList);
    }

    @GetMapping("/order/showAllOptions/menuId/{menuId}")
    @ApiOperation(value = "모든 옵션 보여주기", notes = "메뉴별 선택할 수 있는 옵션이 다르며, 리스트 형식으로 반환합니다.")
    public ResponseEntity<List<OptionsItem>> showAllOptions(@PathVariable Long menuId) {

        List<OptionsItem> optionsList = orderService.showAllOptions(menuId);
        return ResponseEntity.ok().body(optionsList);
    }

    @GetMapping("/order/showAllStyles/menuId/{menuId}")
    @ApiOperation(value = "모든 스타일 보여주기", notes = "메뉴별 선택할 수 있는 스타일이 다르며, 리스트 형식으로 반환합니다.")
    public ResponseEntity<List<StyleItem>> showAllStyles(@PathVariable Long menuId) {

        List<StyleItem> styleList = orderService.showAllStyles(menuId);
        return ResponseEntity.ok().body(styleList);
    }
}
