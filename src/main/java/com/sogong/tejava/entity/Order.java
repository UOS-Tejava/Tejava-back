package com.sogong.tejava.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.customer.Menu;
import com.sogong.tejava.entity.customer.OrderHistory;
import com.sogong.tejava.entity.customer.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "order_table")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double total_price;
    private String order_status;
    private String req_orderDateTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_history_id")
    private OrderHistory orderHistory;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "order_table_id", referencedColumnName = "id")
    private List<Menu> menu;

    public static Order createOrder(User user) {
        Order order = new Order();
        order.setOrderHistory(user.getOrderHistory());
        order.setMenu(null);
        order.setTotal_price(0.0);
        order.setOrder_status("");

        return order;
    }
}