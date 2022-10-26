package com.sogong.tejava.dto;

import com.sogong.tejava.entity.customer.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDTO {

    private Long userId;
    private ShoppingCart shoppingCart;
}
