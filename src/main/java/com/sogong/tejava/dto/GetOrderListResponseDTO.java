package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderListResponseDTO {

    private List<GetOrderListResponseItemDTO> orderList;
    private int chef;
    private int delivery;
}
