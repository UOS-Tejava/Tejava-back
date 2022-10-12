package com.sogong.tejava.repository;

import com.sogong.tejava.entity.style.StyleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleItemRepository extends JpaRepository<StyleItem, Long> {
}
