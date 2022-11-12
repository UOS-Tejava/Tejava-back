package com.sogong.tejava.repository;

import com.sogong.tejava.entity.customer.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {

    void deleteAllByMenuId(Long menuId);
}