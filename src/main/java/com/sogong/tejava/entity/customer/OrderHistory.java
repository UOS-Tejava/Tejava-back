package com.sogong.tejava.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.BaseTimeEntity;
import com.sogong.tejava.entity.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "order_history")
public class OrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "orderHistory")
    private List<Order> order = new ArrayList<>();
}
