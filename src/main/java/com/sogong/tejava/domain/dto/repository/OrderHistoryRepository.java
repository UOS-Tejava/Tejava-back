package com.sogong.tejava.domain.dto.repository;

import com.sogong.tejava.entity.customer.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    OrderHistory findByUserId(Long userId);

    OrderHistory findByOrderId(Long orderId);
}