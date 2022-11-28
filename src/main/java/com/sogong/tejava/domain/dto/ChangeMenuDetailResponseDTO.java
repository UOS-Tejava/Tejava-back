package com.sogong.tejava.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMenuDetailResponseDTO {

    private double pre_price;
    private double post_price;
}