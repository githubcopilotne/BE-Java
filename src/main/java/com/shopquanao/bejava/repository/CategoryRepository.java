package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository truy vấn bảng Categories
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Interface Projection — Spring tự sinh SQL chỉ lấy 4 field
    List<CategoryListProjection> findAllBy();
}
