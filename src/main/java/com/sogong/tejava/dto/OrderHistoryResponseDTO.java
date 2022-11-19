package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class OrderHistoryResponseDTO {

    private Long menuId;
    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;
    private int quantity;

    private List<OptionsDTO> options;
    private StyleDTO style;

    private String customerName;
    private String customerAddress;
    private String orderDateTime;
    private String req_orderDateTime;


    public static OrderHistoryResponseDTO from(Menu menu) {

        List<OptionsDTO> optionsDTOList = menu.getOptions().stream().map(OptionsDTO::from).collect(Collectors.toList());
        StyleDTO styleDTO = StyleDTO.from(menu);

        return OrderHistoryResponseDTO.builder()
                .menuId(menu.getId()) // order 테이블에서 주문 상태에 접근을 위해 필요함
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
