package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Order;
import com.sogong.tejava.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class OrderDTO {

    private double total_price;
    private OrderStatus order_status;
    private String option_to_string;
    private List<MenuDTO> menuList;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .total_price(order.getTotal_price())
                .order_status(order.getOrder_status())
                .option_to_string(order.getOption_to_string())
                .menuList(order.getMenu().stream().map(MenuDTO::from).collect(Collectors.toList()))
                .build();
    }
}
