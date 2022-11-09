package com.sogong.tejava.repository;

import com.sogong.tejava.entity.customer.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUid(String uid);

    User findUserByUid(String uid);

    User findUserById(Long userId);
}