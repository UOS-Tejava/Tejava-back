package com.sogong.tejava.repository;

import com.sogong.tejava.entity.employee.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockItem, Long> {
}
