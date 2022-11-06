package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDTO {

    private Long userId;
    private double total_price;
    private String orderDateTime;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .userId(order.getOrderHistory().getUser().getId())
                .total_price(order.getTotal_price())
                .orderDateTime(order.getCreatedDate().toString())
                .build();
    }
}
