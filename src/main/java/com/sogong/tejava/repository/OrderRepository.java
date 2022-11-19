package com.sogong.tejava.repository;

import com.sogong.tejava.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(Long orderId);

    Order findOrderByMenuId(Long menuId);

    List<Order> findAllByOrderHistoryId(Long orderHistoryId);
}