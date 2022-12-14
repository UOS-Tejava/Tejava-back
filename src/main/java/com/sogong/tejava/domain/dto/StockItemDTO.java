package com.sogong.tejava.domain.dto;

import com.sogong.tejava.entity.employee.StockItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockItemDTO {

    private Long id;
    private String stock_item_nm;
    private String stock_item_pic;
    private int quantity;

    public static StockItemDTO from(StockItem stockItem) {
        return StockItemDTO.builder()
                .id(stockItem.getId())
                .stock_item_nm(stockItem.getStock_item_nm())
                .stock_item_pic(stockItem.getStock_item_pic())
                .quantity(stockItem.getQuantity())
                .build();
    }
}