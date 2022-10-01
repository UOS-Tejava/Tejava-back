package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String styleNm;
    private String styleConfig;
    private String imagePath;
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
