package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderHistoryDTO {

    private String customerName;
    private String customerAddress;

    private List<MenuDTO> menus;
    //////////////////////////// 윗 부분은 코드에서 직접 set
    private String order_status;
    private String orderDateTime;
    private String req_orderDateTime;
    private double total_price;

    public static OrderHistoryDTO from(Order order) {

        return OrderHistoryDTO.builder()
                .order_status(order.getOrder_status())
                .orderDateTime(order.getCreatedDate())
                .req_orderDateTime(order.getReq_orderDateTime())
                .total_price(order.getTotal_price())
                .build();
    }
}