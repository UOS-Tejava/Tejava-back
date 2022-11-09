package com.sogong.tejava.controller;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.entity.customer.Menu;
import com.sogong.tejava.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ShoppingCartController {

    private final CartService cartService;

    public ShoppingCartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 카트에 담긴 메뉴 아이템 조회하기
    @GetMapping("/cart")
    @ApiOperation(value = "장바구니 조회하기", notes = "장바구니에 담긴 메뉴가 리스트 형태로 반환됩니다.")
    public ResponseEntity<List<MenuDTO>> showCartItems(HttpServletRequest request) {

        List<MenuDTO> menuList = cartService.showCartItems(request);
        return ResponseEntity.ok().body(menuList);
    }

    // 카트에 메뉴 아이템 추가하기
    @PostMapping("/cart/add")
    @ApiOperation(value = "장바구니에 메뉴 아이템 추가하기", notes = "장바구니 목록에 메뉴가 추가됩니다.")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartDTO addToCartDTO) {

        cartService.addToCart(addToCartDTO);
        return ResponseEntity.ok().build();
    }

    // 카트에 담긴 메뉴 아이템의 옵션/스타일(menu detail) 수정하기
    @PatchMapping("/cart/update/menu-detail")
    @ApiOperation(value = "장바구니의 메뉴 옵션/스타일 수정하기", notes = "장바구니 목록에서 아이템을 선택하여 옵션/스타일을 수정합니다.\n여기선 orderId의 값을 주지 않아도 됩니다.")
    public ResponseEntity<?> updateMenuDetail(@RequestBody ChangeMenuDetailDTO changeMenuDetailDTO) {

        cartService.updateMenuDetail(changeMenuDetailDTO);
        return ResponseEntity.ok().build();
    }

    // 카트의 메뉴 아이템 한 개 삭제하기
    @DeleteMapping("/cart/delete-one/menuId/{menuId}")
    @ApiOperation(value = "장바구니에서 메뉴 한 개 삭제하기", notes = "장바구니 목록에서 하나의 아이템 옆의 휴지통을 클릭하여 실행합니다.")
    public ResponseEntity<?> deleteOne(HttpServletRequest request, @PathVariable Long menuId) {

        cartService.deleteOne(request, menuId);
        return ResponseEntity.ok().build();
    }
}