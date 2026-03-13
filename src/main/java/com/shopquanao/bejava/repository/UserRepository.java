package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.CustomerDetailProjection;
import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import com.shopquanao.bejava.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

        // Native SQL: JOIN Users + Orders để lấy thông tin KH + thống kê đơn hàng trong 1 query
        @Query(value = "SELECT u.user_id AS userId, u.full_name AS fullName, u.email AS email, " +
                        "u.phone AS phone, u.gender AS gender, u.birthday AS birthday, " +
                        "u.address AS address, u.created_at AS createdAt, u.status AS status, " +
                        "COUNT(o.order_id) AS totalOrders, " +
                        "ISNULL(SUM(o.total_money), 0) AS totalSpent " +
                        "FROM Users u LEFT JOIN Orders o ON u.user_id = o.user_id " +
                        "WHERE u.user_id = :id AND u.role = 'Customer' " +
                        "GROUP BY u.user_id, u.full_name, u.email, u.phone, u.gender, " +
                        "u.birthday, u.address, u.created_at, u.status",
                        nativeQuery = true)
        Optional<CustomerDetailProjection> getCustomerById(@Param("id") Integer id);
}
