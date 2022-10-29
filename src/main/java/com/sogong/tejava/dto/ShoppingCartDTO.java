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
    private ShoppingCart shoppingCart; // TODO: 수정 필요 -> 주문 관련해서 코드 수정 시, 변경 예정
}
