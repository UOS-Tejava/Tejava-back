package com.sogong.tejava.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStockInfoDTO {

    private Long employeeId;
    private Long stockItemId;
    private int quantity;
}