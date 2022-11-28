package com.sogong.tejava.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long userId;
    private Long orderId;
    private String customerName;
    private String customerAddress;
    private String orderDateTime;
    private String req_orderDateTime;
    private double totalPrice;
}