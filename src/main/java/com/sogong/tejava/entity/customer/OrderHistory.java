package com.sogong.tejava.entity.customer;

import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Order;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "order_history")
public class OrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "orderHistory")
    private List<Order> order = new ArrayList<>();
}
