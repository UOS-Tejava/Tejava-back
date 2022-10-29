package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.dto.OptionsDTO;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final MenuRepository menuRepository;
    private final StyleRepository styleRepository;
    private final OptionsRepository optionsRepository;
    private final UserRepository userRepository;

    /*
    1. 카트에 담긴 메뉴 보여주기
    2. 카트에 메뉴 담기
    3. 카트에 담겨 있는 아이템의 옵션/스타일 수정
    4. 카트에 담긴 거 아이템 한 개 삭제
    (5. 유저 권한인 지 체크하기)
     */

    // 카트에 담긴 메뉴 보여주기
    public List<Menu> showCartItems(UserIdDTO userIdDTO) { // TODO: 하위 클래스인 style과 options가 매핑이 안되어서 dao를 반환하도록 한 상태 -> 개발 완료 후 수정할 것

        User customer = userRepository.findUserById(userIdDTO.getUserId());
        userRoleCheck(customer.getId());

        List<Menu> menuList = menuRepository.findAllByShoppingCartId(customer.getShoppingCart().getId());
        log.info("menuList style : " + menuList.get(0).getStyle());

        return menuRepository.findAllByShoppingCartId(customer.getShoppingCart().getId());
    }

    @Transactional
    // 카트에 메뉴 담기 : 이때 메뉴의 옵션이나 스타일은 정해져 있는 상태
    public void addToCart(AddToCartDTO addToCartDTO) {

        User customer = userRepository.findUserById(addToCartDTO.getUserId());
        userRoleCheck(addToCartDTO.getUserId());

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(customer.getId());

        // menu 생성 후 저장
        Style style = new Style();
        List<Options> options = new ArrayList<>();
        Menu menu = new Menu();

        menu.setMenu_nm(addToCartDTO.getMenu().getMenu_nm());
        menu.setMenu_pic(addToCartDTO.getMenu().getMenu_pic());
        menu.setMenu_config(addToCartDTO.getMenu().getMenu_config());
        menu.setPrice(addToCartDTO.getMenu().getPrice());
        menu.setQuantity(addToCartDTO.getMenu().getQuantity());
        menu.setShoppingCart(shoppingCart);
        menuRepository.save(menu);


        style.setStyle_nm(addToCartDTO.getMenu().getStyle().getStyle_nm());
        style.setStyle_config((addToCartDTO.getMenu().getStyle().getStyle_config()));
        style.setStyle_pic(addToCartDTO.getMenu().getStyle().getStyle_pic());
        style.setPrice(addToCartDTO.getMenu().getStyle().getPrice());
        style.setMenu(menu);
        styleRepository.save(style);

        for (OptionsDTO option : addToCartDTO.getMenu().getOptions()) {
            Options options1 = new Options();
            options1.setOption_nm(option.getOption_nm());
            options1.setOption_pic(option.getOption_pic());
            options1.setPrice(option.getPrice());
            options1.setQuantity(option.getQuantity());
            options1.setMenu(menu);

            options.add(options1);
        }
        optionsRepository.saveAll(options);

        menu.setStyle(style);
        menu.setOptions(options);

        menuRepository.save(menu);
        shoppingCart.getMenu().add(0, menu);
        log.info(shoppingCart.getMenu().toString()); // 추가된 것 확인

        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    // 카트의 메뉴 디테일(옵션/스타일) 수정하기
    public void updateMenuDetail(ChangeMenuDetailDTO changeMenuDetailDTO) {

        User customer = userRepository.findUserById(changeMenuDetailDTO.getUserId());
        userRoleCheck(changeMenuDetailDTO.getUserId());

        ShoppingCart shoppingCart = customer.getShoppingCart();


        // 수정될 메뉴를 삭제한 후, 새로운 옵션/스타일이 적용된 메뉴를 카트에 담는다
        List<Menu> menuList = menuRepository.findAllByShoppingCartId(shoppingCart.getId());
        log.info("수정될 메뉴 후보 : " + menuList);

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

        optionsRepository.deleteAllByMenuId(menuTmp.getId());
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

        // 카트에 수정된 메뉴 추가
        shoppingCart.getMenu().add(0, menu);

        // db에 갱신
        menuRepository.save(menu);
        menuRepository.delete(menuTmp);
        shoppingCart.setMenu(menuRepository.findAllByShoppingCartId(shoppingCart.getId()));
        shoppingCartRepository.save(shoppingCart);
    }

    // 카트의 메뉴 아이템 하나 삭제하기
    public void deleteOne(CancelMenuFromCartDTO cancelMenuFromCartDTO) { // TODO : MenuId 구별 확인할 것

        User customer = userRepository.findUserById(cancelMenuFromCartDTO.getUserId());
        userRoleCheck(cancelMenuFromCartDTO.getUserId());

        ShoppingCart shoppingCart = customer.getShoppingCart();

        menuRepository.deleteById(cancelMenuFromCartDTO.getMenuId());
        shoppingCart.setMenu(menuRepository.findAllByShoppingCartId(shoppingCart.getId()));
        shoppingCartRepository.save(shoppingCart);
    }

    // 관리자 권한이 있는 지 확인하기
    public void userRoleCheck(Long userId) {

        User user = userRepository.findUserById(userId);

        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("일반 회원이 사용하실 수 있는 기능입니다.");
        }
    }
}
