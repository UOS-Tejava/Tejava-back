package com.sogong.tejava.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "shopping_cart")
public class ShoppingCart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double total_price;

    @JsonIgnore
    @OneToMany(mappedBy = "shoppingCart", fetch = FetchType.EAGER)
    private List<Menu> menu = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;

    public static ShoppingCart createCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMenu(null);
        shoppingCart.setTotal_price(0.0);
        shoppingCart.user = user;

        return shoppingCart;
    }
}