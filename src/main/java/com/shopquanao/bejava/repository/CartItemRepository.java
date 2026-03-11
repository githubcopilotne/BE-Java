package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Kiểm tra variant có trong giỏ hàng không
    boolean existsByVariantId(Integer variantId);

    // Xoá tất cả cart_items tham chiếu tới variant
    @Transactional
    void deleteByVariantId(Integer variantId);

    // Xoá cart_items theo nhiều variant (dùng khi xoá product)
    void deleteByVariantIdIn(List<Integer> variantIds);
}
