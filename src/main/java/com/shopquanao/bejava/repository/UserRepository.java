package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import com.shopquanao.bejava.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

        @Query("SELECT u FROM User u WHERE u.role = 'Customer' " +
                        "AND (:keyword IS NULL OR u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%) "
                        +
                        "AND (:status IS NULL OR u.status = :status)")
        Page<CustomerListProjection> getCustomers(
                        @Param("keyword") String keyword,
                        @Param("status") Integer status,
                        Pageable pageable);
}
