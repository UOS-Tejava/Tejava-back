package com.sogong.tejava.domain.dto.repository;

import com.sogong.tejava.entity.customer.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {

    void deleteAllByMenuId(Long menuId);

    List<Options> findAllByMenuId(Long menuId);
}