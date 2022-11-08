package com.sogong.tejava.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDTO {

    private Long userId;
    private double total_price;
    private String req_orderDateTime;
}
