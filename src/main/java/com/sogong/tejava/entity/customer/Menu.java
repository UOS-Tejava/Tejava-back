package com.sogong.tejava.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.Order;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "menu")
@ToString
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menu_nm;
    private String menu_config;
    private String menu_pic;
    private int price;
    private int quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_table_id")
    private Order order;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.REMOVE)
    private List<Options> options = new ArrayList<>();

    @OneToOne(mappedBy = "menu", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Style style;
}
