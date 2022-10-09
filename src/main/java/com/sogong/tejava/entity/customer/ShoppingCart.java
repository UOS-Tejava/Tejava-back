package com.sogong.tejava.entity.customer;

import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Menu;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "shoppingCart")
public class ShoppingCart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalPrice;

    @OneToMany(mappedBy = "shoppingCart")
    private List<Menu> menu = new ArrayList<>();

    @OneToOne
    private User user;
}
