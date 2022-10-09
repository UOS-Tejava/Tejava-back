package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String customerName;
    private String customerAddress;
    private String orderDateAndTime;
    private double totalPrice;
}
