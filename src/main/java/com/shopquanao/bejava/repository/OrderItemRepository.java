package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Kiểm tra variant đã có trong đơn hàng chưa
    boolean existsByVariantId(Integer variantId);

    // Check nhiều variant có đơn hàng không (dùng khi xoá product)
    boolean existsByVariantIdIn(List<Integer> variantIds);
}
