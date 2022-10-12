package com.sogong.tejava.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Menu;
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
    @OneToMany(mappedBy = "shoppingCart")
    private List<Menu> menu = new ArrayList<>();

    @JsonIgnore
    @OneToOne
    private User user;
}
