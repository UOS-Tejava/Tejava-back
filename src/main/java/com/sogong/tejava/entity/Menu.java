package com.sogong.tejava.entity;

import com.sogong.tejava.entity.customer.ShoppingCart;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "menu")
@ToString
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuNm;
    private String menuConfig;
    private String menuPic;
    private int price;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoppingCart_id")
    private ShoppingCart shoppingCart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "menu")
    private List<Options> options = new ArrayList<>();

    @OneToOne(mappedBy = "menu")
    private Style style;
}
