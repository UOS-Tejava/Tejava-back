package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionNm;
    private String imagePath;
    private Integer price;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
