package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Options;
import com.sogong.tejava.entity.options.OptionsItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OptionsDTO {

    private String option_nm;
    private String option_pic;
    private int price;
    private int quantity;

    public static OptionsDTO from(Options option){
        return OptionsDTO.builder()
                .option_nm(option.getOption_nm())
                .option_pic(option.getOption_pic())
                .price(option.getPrice())
                .quantity(option.getQuantity())
                .build();
    }

    public static OptionsDTO from(OptionsItem option){
        return OptionsDTO.builder()
                .option_nm(option.getOption_nm())
                .option_pic(option.getOption_pic())
                .price(option.getPrice())
                .quantity(option.getQuantity())
                .build();
    }
}
