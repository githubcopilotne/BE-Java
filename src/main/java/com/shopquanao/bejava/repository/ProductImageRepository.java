package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository truy vấn bảng Product_Images
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    // Kế thừa save, saveAll từ JpaRepository — đủ dùng cho POST images

    // Check product có ảnh chính không
    boolean existsByProductIdAndIsMainTrue(Integer productId);

    // Tìm ảnh chính hiện tại của product
    ProductImage findByProductIdAndIsMainTrue(Integer productId);
}
