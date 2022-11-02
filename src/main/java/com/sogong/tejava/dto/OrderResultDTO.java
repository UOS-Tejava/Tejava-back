package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResultDTO {

    private String customerName;
    private String customerAddress;
    private String orderDateTime;
    private double totalPrice;
}
