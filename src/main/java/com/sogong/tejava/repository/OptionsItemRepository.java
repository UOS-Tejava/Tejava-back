package com.sogong.tejava.repository;

import com.sogong.tejava.entity.options.OptionsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsItemRepository extends JpaRepository<OptionsItem, Long> {
}
