package com.sogong.tejava.dto;

import com.sogong.tejava.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartDTO {

    private Long userId;
    private Menu menu;
}
