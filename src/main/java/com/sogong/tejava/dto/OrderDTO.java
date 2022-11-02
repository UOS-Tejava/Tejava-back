package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class OrderDTO {

    private Long userId;
    private double total_price;
    private String order_status;
    private List<MenuDTO> shoppingCartItems;
    private String orderDateTime;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .total_price(order.getTotal_price())
                .order_status(order.getOrder_status())
                .shoppingCartItems(order.getMenu().stream().map(MenuDTO::from).collect(Collectors.toList()))
                .orderDateTime(order.getCreatedDate().toString())
                .build();
    }
}
