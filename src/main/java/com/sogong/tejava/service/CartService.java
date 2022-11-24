package com.sogong.tejava.service;

import com.sogong.tejava.dto.*;
import com.sogong.tejava.dto.OptionsDTO;
import com.sogong.tejava.entity.customer.*;
import com.sogong.tejava.entity.Role;
import com.sogong.tejava.entity.employee.StockItem;
import com.sogong.tejava.repository.*;
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
public class CartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final MenuRepository menuRepository;
    private final StyleRepository styleRepository;
    private final OptionsRepository optionsRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    /*
    1. 카트에 담긴 메뉴 보여주기
    2. 카트에 메뉴 담기
    3. 카트에 담겨 있는 아이템의 옵션/스타일 수정
    4. 카트에 담긴 거 아이템 한 개 삭제
    (5. 유저 권한인 지 체크하기)
    (6. 메뉴가 기존 메뉴와 동일(옵션/스타일)한 지 체크하기)
    (7. 요청으로부터 회원 객체 반환하기)
    (8. 재고현황 체크)
     */

    // 카트에 담긴 메뉴 보여주기
    public List<MenuDTO> showCartItems(HttpServletRequest request) {

        User user = getUserFromRequest(request);
        userRoleCheck(user.getId());

        return menuRepository.findAllByShoppingCartId(shoppingCartRepository.findByUserId(user.getId()).getId()).stream().map(MenuDTO::from).collect(Collectors.toList());
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

        // 스타일과 옵션 제외하고 세팅
        menu.setMenu_nm(addToCartDTO.getMenu().getMenu_nm());
        menu.setMenu_pic(addToCartDTO.getMenu().getMenu_pic());
        menu.setMenu_config(addToCartDTO.getMenu().getMenu_config());
        menu.setPrice(addToCartDTO.getMenu().getPrice());
        menu.setQuantity(addToCartDTO.getMenu().getQuantity());
        menu.setShoppingCart(shoppingCart);
        menuRepository.save(menu);

        // 스타일 세팅
        style.setStyle_nm(addToCartDTO.getMenu().getStyle().getStyle_nm());
        style.setStyle_config((addToCartDTO.getMenu().getStyle().getStyle_config()));
        style.setStyle_pic(addToCartDTO.getMenu().getStyle().getStyle_pic());
        style.setPrice(addToCartDTO.getMenu().getStyle().getPrice());
        style.setMenu(menu);
        styleRepository.save(style);

        // 옵션 세팅
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

        // 메뉴에 스타일과 옵션 세팅
        menu.setStyle(style);
        menu.setOptions(options);

        List<Menu> menuList = menuRepository.findAllByShoppingCartId(shoppingCart.getId());

        stockQuantityCheck(menuList);

        log.info("모든 메뉴 리스트 : " + menuList);

        if (findMenuDuplicate(menu, menuList) != null) { // 중복되는 메뉴라면

            if (menuList.size() > 1) {
                Menu menu1 = findMenuDuplicate(menu, menuList); // 기존 메뉴를 찾은 이후
                menu1.setPrice(menu1.getPrice() + menu.getPrice()); // 기존 메뉴의 가격을 수량을 고려하여 올리고
                menu1.setQuantity(menu1.getQuantity() + menu.getQuantity()); // 기존 메뉴의 수량을 추가하려했던 메뉴의 수량만큼 올리고
                menuRepository.save(menu1); // db에 갱신하고
                menuRepository.delete(menu); // 추가했던 메뉴는 다시 삭제
                shoppingCart.setMenu(menuRepository.findAll()); // 수량만 바뀐 메뉴를 장바구니에 반영
            }
        } else { // 중복된 메뉴가 아니라면 그대로 저장
            menuRepository.save(menu);
            shoppingCart.getMenu().add(menu);
        }

        // 카트에도 메뉴 추가 후 db에 갱신
        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    // 카트의 메뉴 디테일(옵션/스타일) 수정하기
    public void updateMenuDetail(ChangeMenuDetailDTO changeMenuDetailDTO) {

        User customer = userRepository.findUserById(changeMenuDetailDTO.getUserId());
        userRoleCheck(changeMenuDetailDTO.getUserId());

        ShoppingCart shoppingCart = customer.getShoppingCart();

        // 수정될 메뉴를 가져와 옵션/스타일의 가격을 메뉴의 가격에서 차감하면서 초기화
        Menu menu = menuRepository.getMenuById(changeMenuDetailDTO.getMenuId()); // 수정할 메뉴를 가져옴
        int price = menu.getPrice();

        for (Options option : optionsRepository.findAllByMenuId(menu.getId())) {
            price -= option.getPrice() * option.getQuantity() * menu.getQuantity();
            optionsRepository.delete(option);
        }

        Style oldStyle = styleRepository.findStyleByMenuId(menu.getId());
        price -= oldStyle.getPrice() * menu.getQuantity();
        styleRepository.delete(oldStyle);

        optionsRepository.deleteAllByMenuId(menu.getId());
        styleRepository.deleteByMenuId(menu.getId());
        menuRepository.save(menu);

        log.info("수정할 메뉴 : " + menu);

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
            price += option.getPrice() * option.getQuantity() * menu.getQuantity();
        }
        optionsRepository.saveAll(newOptions);

        // 새로운 스타일 생성
        Style style = new Style();
        style.setStyle_nm(changeMenuDetailDTO.getNewStyle().getStyle_nm());
        style.setStyle_pic(changeMenuDetailDTO.getNewStyle().getStyle_pic());
        style.setStyle_config(changeMenuDetailDTO.getNewStyle().getStyle_config());
        style.setPrice(changeMenuDetailDTO.getNewStyle().getPrice());
        style.setMenu(menu);

        price += style.getPrice() * menu.getQuantity();
        styleRepository.save(style);

        // 새로 생성한 메뉴에 새로운 옵션/스타일, 가격 반영
        menu.setOptions(newOptions);
        menu.setStyle(style);
        menu.setPrice(price);

        // 중복 체크
        List<Menu> menuList = menuRepository.findAllByShoppingCartId(shoppingCart.getId());

        log.info("모든 메뉴 리스트 : " + menuList);

        if (findMenuDuplicate(menu, menuList) != null) { // 중복되는 메뉴라면

            if (menuList.size() > 1) {
                Menu menu1 = findMenuDuplicate(menu, menuList); // 기존 메뉴를 찾은 이후
                menu1.setPrice(menu1.getPrice() + menu.getPrice()); // 기존 메뉴의 가격을 수량을 고려하여 올리고
                menu1.setQuantity(menu1.getQuantity() + menu.getQuantity()); // 기존 메뉴의 수량을 추가하려했던 메뉴의 수량만큼 올리고
                log.info("new price is " + menu1.getPrice() + " * (" + menu.getQuantity() + " + " + menu1.getQuantity() + ") = " + menu1.getPrice() * (menu.getQuantity() + menu1.getQuantity()));
                menuRepository.save(menu1); // db에 갱신하고
                menuRepository.delete(menu);
                shoppingCart.setMenu(menuRepository.findAll()); // 수량과 가격이 바뀐 메뉴를 장바구니에 반영
            }
        } else {
            // 중복되는 메뉴가 없다면
            menuRepository.save(menu);
            shoppingCart.setMenu(menuRepository.findAll());
        }

        // 카트에도 메뉴 추가 후 db에 갱신
        shoppingCartRepository.save(shoppingCart);
    }

    // 카트의 메뉴 아이템 하나 삭제하기
    @Transactional
    public void deleteOne(HttpServletRequest request, Long menuId) {

        User user = getUserFromRequest(request);
        userRoleCheck(user.getId());

        ShoppingCart shoppingCart = user.getShoppingCart();

        menuRepository.deleteById(menuId);
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

    // menuList 안에 menu 와 동일한 메뉴가 있는 지 확인하고 중복되는 게 있다면 기존 메뉴를 반환
    public Menu findMenuDuplicate(Menu menu, List<Menu> menuList) { // addToCart 로직에선 menu 가 이미 db에 저장되어 있기 때문에 menu 를 제외하고 동일한 메뉴가 있는 지 파악해야 함

        for (Menu item : menuList) {
            if (!menu.getId().equals(item.getId()) && menu.getMenu_nm().equals(item.getMenu_nm())) { // 같은 메뉴를 추가하는 경우

                List<Options> item_optionList = item.getOptions();
                List<Options> menu_optionList = menu.getOptions();

                Style item_style = item.getStyle();
                Style menu_style = item.getStyle();

                boolean result = true;
                boolean sameMenu;
                if (item_optionList.size() == menu_optionList.size()) {
                    for (Options item_option : item_optionList) { // 추가적인 조건 : 옵션 이름,수량 그리고 스타일을 이름이 동일하면 동일한 메뉴
                        sameMenu = false;
                        for (Options menu_option : menu_optionList) {
                            if (item_option.getOption_nm().equals(menu_option.getOption_nm()) && item_option.getQuantity() == menu_option.getQuantity() && item_style.getStyle_nm().equals(menu_style.getStyle_nm())) {
                                sameMenu = true;
                                break;
                            }
                        }
                        result &= sameMenu;
                    }
                }

                if (result) {
                    return item;
                }
            }
        }
        return null;
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

    // 재고현황 체크
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
                            throw new IllegalStateException("와인의 재고가 부족합니다.");
                        }
                    case "커피 한 잔":
                        if (option.getQuantity() > coffee.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("커피의 재고가 부족합니다.");
                        }
                    case "치즈":
                        if (option.getQuantity() > cheese.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("치즈의 재고가 부족합니다.");
                        }
                    case "샐러드":
                        if (option.getQuantity() > salad.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("샐러드의 재고가 부족합니다.");
                        }
                    case "빵":
                    case "바게트 빵":
                        if (option.getQuantity() > bread.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("빵의 재고가 부족합니다.");
                        }
                    case "샴페인 한 병":
                        if (option.getQuantity() > champagne.getQuantity()) {
                            optionsRepository.delete(option);
                            throw new IllegalStateException("샴페인의 재고가 부족합니다.");
                        }
                }
            }
        }
    }
}