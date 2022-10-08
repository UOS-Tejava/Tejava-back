package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "options")
public class Options {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionNm;
    private String optionPic;
    private int price;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
