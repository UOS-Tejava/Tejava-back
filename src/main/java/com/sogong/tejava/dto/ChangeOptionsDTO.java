package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.Options;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOptionsDTO {

    private Long userId;
    private Long orderId;
    private Long menuId;
    private List<Options> newOptions;
}
