package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDTO {

    private Long userId;
    private String customerName;
    private String customerAddress;
    private double total_price;
    private String req_orderDateTime;
}