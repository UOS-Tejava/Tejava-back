package com.sogong.tejava.dto;

import com.sogong.tejava.entity.menu.MenuItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MenuItemDTO {

    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;

    public static MenuItemDTO from(MenuItem menu){
        return MenuItemDTO.builder()
                .menu_config(menu.getMenu_config())
                .menu_nm(menu.getMenu_nm())
                .menu_pic(menu.getMenu_pic())
                .price(menu.getPrice())
                .build();
    }
}


