package com.sogong.tejava.dto;

import com.sogong.tejava.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOrderStatusDTO {

    private Long userId;
    private Long orderId;
    private OrderStatus orderStatus;
}