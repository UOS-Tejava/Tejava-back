package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuNm;
    private String menuConfig; // 메뉴 구성요소
    private String imagePath;
    private Integer price;

    @OneToMany(mappedBy = "menu")
    private List<OrderHistoryMenu> orderHistoryMenu = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<Options> options = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<Style> style = new ArrayList<>();
}
