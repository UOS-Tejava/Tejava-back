package com.sogong.tejava.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOrderStatusDTO {

    private Long employeeId;
    private Long orderId;
    private String orderStatus;
}