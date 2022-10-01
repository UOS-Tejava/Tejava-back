package com.sogong.tejava.entity.employee;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemNm;
    private String imagePath;
    private Integer quantity;
}
