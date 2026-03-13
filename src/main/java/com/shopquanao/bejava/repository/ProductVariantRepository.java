package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository truy vấn bảng Product_Variants
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    // Lấy tất cả variants của 1 product
    List<ProductVariant> findByProductId(Integer productId);

    // Check product có variant nào không
    boolean existsByProductId(Integer productId);
}
