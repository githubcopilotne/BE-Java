package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// Repository truy vấn bảng Products
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Kiểm tra có sản phẩm nào thuộc danh mục này không
    boolean existsByCategoryId(Integer categoryId);

    // Lấy danh sách sản phẩm (phân trang) — JOIN 3 bảng để lấy categoryName,
    // mainImageUrl, totalStock
    @Query(value = """
            SELECT p.product_id AS productId,
                   p.product_name AS productName,
                   p.unit_price AS unitPrice,
                   c.category_name AS categoryName,
                   (SELECT TOP 1 pi.image_url FROM Product_Images pi
                    WHERE pi.product_id = p.product_id AND pi.is_main = 1) AS mainImageUrl,
                   (SELECT COALESCE(SUM(pv.stock_quantity), 0) FROM Product_Variants pv
                    WHERE pv.product_id = p.product_id) AS totalStock,
                   p.status AS status,
                   p.created_at AS createdAt
            FROM Products p
            JOIN Categories c ON p.category_id = c.category_id
            """, countQuery = "SELECT COUNT(*) FROM Products", nativeQuery = true)
    Page<ProductListProjection> findAllProducts(Pageable pageable);

    // Kiểm tra tên sản phẩm đã tồn tại chưa (SQL Server mặc định case-insensitive)
    boolean existsByProductName(String productName);
}
