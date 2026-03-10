package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Kiểm tra variant có trong giỏ hàng không
    boolean existsByVariantId(Integer variantId);

    // Xoá tất cả cart_items tham chiếu tới variant
    @Transactional
    void deleteByVariantId(Integer variantId);
}
