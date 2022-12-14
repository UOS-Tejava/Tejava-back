package com.sogong.tejava.domain.dto.repository;

import com.sogong.tejava.entity.customer.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Menu getMenuById(Long menuId);

    List<Menu> findAllByShoppingCartId(Long shoppingCartId);

    List<Menu> findAllByOrderId(Long orderId);
}