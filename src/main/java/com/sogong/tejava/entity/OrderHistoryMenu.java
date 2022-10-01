package com.sogong.tejava.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class OrderHistoryMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name = "orderHistory_id")
    private OrderHistory orderHistory;

    @ManyToOne(fetch = FetchType.LAZY) // Many = OrderHistory, = One 한명의 유저는 여러 개의 주문 내역을 갖고 있다.
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
