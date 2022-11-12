package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Menu;
import com.sogong.tejava.entity.style.StyleItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StyleDTO {

    private String style_nm;
    private String style_config;
    private String style_pic;
    private int price;

    public static StyleDTO from(Menu menu) {
        return StyleDTO.builder()
                .style_nm(menu.getStyle().getStyle_nm())
                .style_config(menu.getStyle().getStyle_config())
                .style_pic(menu.getStyle().getStyle_pic())
                .price(menu.getStyle().getPrice())
                .build();
    }

    public static StyleDTO from(StyleItem style) {
        return StyleDTO.builder()
                .style_nm(style.getStyle_nm())
                .style_config(style.getStyle_config())
                .style_pic(style.getStyle_pic())
                .price(style.getPrice())
                .build();
    }
}