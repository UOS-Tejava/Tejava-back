package com.sogong.tejava.entity.customer;

import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Menu;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "shopping_cart")
public class ShoppingCart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double total_price;

    @OneToMany(mappedBy = "shoppingCart")
    private List<Menu> menu = new ArrayList<>();

    @OneToOne
    private User user;

}
