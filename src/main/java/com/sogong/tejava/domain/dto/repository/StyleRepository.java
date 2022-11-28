package com.sogong.tejava.domain.dto.repository;

import com.sogong.tejava.entity.customer.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {

    void deleteByMenuId(Long menuId);

    Style findStyleByMenuId(Long menuId);
}