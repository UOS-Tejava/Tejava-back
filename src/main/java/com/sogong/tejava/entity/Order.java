package com.sogong.tejava.entity;

import com.sogong.tejava.entity.customer.OrderHistory;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "order")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalPrice;
    private OrderStatus orderStatus;
    private String optionToString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderHistory_id")
    private OrderHistory orderHistory;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private List<Menu> menu;

}
