package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "style")
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String styleNm;
    private String styleConfig;
    private String stylePic;
    private int price;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
