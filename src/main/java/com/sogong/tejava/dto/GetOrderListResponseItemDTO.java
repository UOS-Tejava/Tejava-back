package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderListResponseItemDTO {

    private Long orderId;
    private String orderedDate; // 주문이 이루어진 시간
    private String customerName;
    private String customerAddress;
    private List<MenuDTO> menuDTOList;
    private double totalPrice;
    private String req_orderDateTime; // 고객이 요청하는 주문 시간
    private String orderStatus;
}