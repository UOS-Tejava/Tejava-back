package com.sogong.tejava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMenuDetailDTO {

    private Long userId;
    private Long orderId;
    private Long menuId;
    private List<OptionsDTO> newOptions;
    private StyleDTO newStyle;
}
