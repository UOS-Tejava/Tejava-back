package com.sogong.tejava.dto;

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
    private StyleDTO newStyle;
}
