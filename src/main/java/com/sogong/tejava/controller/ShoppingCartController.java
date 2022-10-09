package com.sogong.tejava.controller;

import com.sogong.tejava.dto.ChangeOptionsDTO;
import com.sogong.tejava.dto.ChangeStyleDTO;
import com.sogong.tejava.entity.Menu;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.service.CartService;
import com.sogong.tejava.util.SessionConst;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShoppingCartController {

    private final CartService cartService;

    public ShoppingCartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 카트에 담긴 메뉴 아이템 조회하기
    @GetMapping("/cart")
    @ApiOperation(value = "장바구니 조회하기", notes = "장바구니 목록의 메뉴가 리스트 형태로 반환됩니다.")
    public ResponseEntity<List<Menu>> showCartItems(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User customer) {

        List<Menu> menuList = cartService.showCartItems(customer);
        return ResponseEntity.ok().body(menuList);
    }

    // 카트에 메뉴 아이템 추가하기
    @PostMapping("/cart/add")
    @ApiOperation(value = "장바구니에 메뉴 아이템 추가하기", notes = "장바구니 목록에 메뉴가 추가됩니다.")
    public ResponseEntity<?> addToCart(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User customer, @RequestBody Menu menu) {

        cartService.addToCart(customer, menu);
        return ResponseEntity.ok().build();
    }

    // 카트에 담긴 메뉴 아이템의 옵션 수정하기
    @PatchMapping("/cart/update/options")
    @ApiOperation(value = "장바구니의 메뉴 옵션 수정하기", notes = "장바구니 목록에서 아이템을 선택하여 옵션을 수정합니다.")
    public ResponseEntity<?> updateMenuOptions(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User customer, @RequestBody ChangeOptionsDTO changeOptionsDTO) {

        cartService.updateMenuOptions(customer, changeOptionsDTO);
        return ResponseEntity.ok().build();
    }

    // 카트에 담긴 메뉴 아이템의 스타일 수정하기
    @PatchMapping("/cart/update/style")
    @ApiOperation(value = "장바구니의 메뉴 스타일 수정하기", notes = "장바구니 목록에서 아이템을 선택하여 스타일을 수정합니다.")
    public ResponseEntity<?> updateMenuOptions(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User customer, @RequestBody ChangeStyleDTO changeStyleDTO) {

        cartService.updateMenuStyle(customer, changeStyleDTO);
        return ResponseEntity.ok().build();
    }

    // 카트의 메뉴 아이템 한 개 삭제하기
    @DeleteMapping("/cart/delete-one/{menuId}")
    @ApiOperation(value = "장바구니에서 메뉴 한 개 삭제하기", notes = "장바구니 목록에서 하나의 아이템 옆의 휴지통을 클릭하여 실행합니다.")
    public ResponseEntity<?> deleteOne(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User customer, @PathVariable Long menuId) {

        cartService.deleteOne(customer, menuId);
        return ResponseEntity.ok().build();
    }
}
