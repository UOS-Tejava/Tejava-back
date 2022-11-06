package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStockInfoDTO {

    private Long userId;
    private Long stockItemId;
    private int quantity;
}