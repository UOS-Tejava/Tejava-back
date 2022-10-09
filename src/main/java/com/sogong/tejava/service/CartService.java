package com.sogong.tejava.service;

import com.sogong.tejava.dto.ChangeOptionsDTO;
import com.sogong.tejava.dto.ChangeStyleDTO;
import com.sogong.tejava.entity.Menu;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.customer.ShoppingCart;
import com.sogong.tejava.entity.customer.User;
import com.sogong.tejava.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    /*
    1. 카트에 담긴 메뉴 보여주기
    2. 카트에 메뉴 담기
    3. 카트에 담겨 있는 아이템의 옵션/스타일 수정
    4. 카트에 담긴 거 아이템 한 개 삭제
     */

    // 카트에 담긴 메뉴 보여주기
    public List<Menu> showCartItems(User customer) {

        validateUser(customer);

        ShoppingCart shoppingCart = customer.getShoppingCart();
        return shoppingCart.getMenu();
    }

    // 카트에 메뉴 담기 : 이때 메뉴의 옵션이나 스타일은 정해져 있는 상태
    public void addToCart(User customer, Menu menu) {

        validateUser(customer);

        ShoppingCart shoppingCart = customer.getShoppingCart();

        shoppingCart.getMenu().add(0, menu);
        shoppingCartRepository.save(shoppingCart);
    }

    // 카트의 메뉴 옵션 수정하기
    public void updateMenuOptions(User customer, ChangeOptionsDTO changeOptionsDTO) {

        validateUser(customer);

        ShoppingCart shoppingCart = customer.getShoppingCart();

        // 메뉴의 옵션 수정
        List<Menu> menuList = shoppingCart.getMenu();
        Menu menu = new Menu();
        for (Menu menu1 : menuList) {
            if (menu1.getId().equals(changeOptionsDTO.getMenuId())) {
                menu = menu1;
                shoppingCart.getMenu().remove(menu1);
            }
        }
        menu.setOptions(changeOptionsDTO.getNewOptions());

        // 장바구니에 수정된 메뉴 추가
        shoppingCart.getMenu().add(0, menu);

        // db에 갱신
        shoppingCartRepository.save(shoppingCart);
    }

    // 카트의 메뉴 스타일 수정하기
    public void updateMenuStyle(User customer, ChangeStyleDTO changeStyleDTO) {

        validateUser(customer);

        ShoppingCart shoppingCart = customer.getShoppingCart();

        // 메뉴의 기존 스타일 수정
        List<Menu> menuList = shoppingCart.getMenu();
        Menu menu = new Menu();
        for (Menu menu1 : menuList) {
            if (menu1.getId().equals(changeStyleDTO.getMenuId())) {
                menu = menu1;
                shoppingCart.getMenu().remove(menu1);
            }
        }
        menu.setStyle(changeStyleDTO.getNewStyle());

        shoppingCart.getMenu().add(0, menu);

        // 장바구니에 수정된 메뉴 추가
        shoppingCartRepository.save(shoppingCart);
    }

    // 카트의 메뉴 아이템 하나 삭제하기
    public void deleteOne(User customer, Long menuId) {

        validateUser(customer);

        ShoppingCart shoppingCart = customer.getShoppingCart();

        // 메뉴의 기존 스타일 수정
        List<Menu> menuList = shoppingCart.getMenu();
        Menu menu = new Menu();
        for (Menu menu1 : menuList) {
            if (menu1.getId().equals(menuId)) {
                menu = menu1;
            }
        }

        shoppingCart.getMenu().remove(menu);

        // 장바구니에 수정된 메뉴 추가
        shoppingCartRepository.save(shoppingCart);
    }

    public void validateUser(User user) {
        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("일반 회원이 사용하실 수 있는 기능입니다.");
        }
    }
}
