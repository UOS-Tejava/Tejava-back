package com.sogong.tejava.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AddToCartMenuDTO {

    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;
    private int quantity;

    private List<OptionsDTO> options;
    private StyleDTO style;
}
