package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Menu;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class MenuDTO {

    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;
    private int quantity;

    private List<OptionsDTO> options;
    private StyleDTO style;

    public static MenuDTO from(Menu menu) {

        List<OptionsDTO> optionsDTOList = menu.getOptions().stream().map(OptionsDTO::from).collect(Collectors.toList());
        StyleDTO styleDTO = StyleDTO.from(menu);

        return MenuDTO.builder()
                .menu_config(menu.getMenu_config())
                .menu_nm(menu.getMenu_nm())
                .menu_pic(menu.getMenu_pic())
                .price(menu.getPrice())
                .quantity(menu.getQuantity())
                .options(optionsDTOList)
                .style(styleDTO)
                .build();
    }
}