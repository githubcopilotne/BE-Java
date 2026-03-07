package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository truy vấn bảng Products
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Kiểm tra có sản phẩm nào thuộc danh mục này không
    boolean existsByCategoryId(Integer categoryId);
}
