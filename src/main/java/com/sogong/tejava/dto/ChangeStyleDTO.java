package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Style;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStyleDTO {

    private Long userId;
    private Long orderId;
    private Long menuId;
    private Style newStyle;
}
