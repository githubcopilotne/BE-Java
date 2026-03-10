package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Kiểm tra variant đã có trong đơn hàng chưa
    boolean existsByVariantId(Integer variantId);
}
