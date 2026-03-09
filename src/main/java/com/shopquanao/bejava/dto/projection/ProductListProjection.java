package com.shopquanao.bejava.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Interface Projection — chỉ SELECT các field cần hiển thị trong danh sách sản phẩm
public interface ProductListProjection {

    Integer getProductId();

    String getProductName();

    BigDecimal getUnitPrice();

    String getCategoryName(); // JOIN từ bảng Categories

    String getMainImageUrl(); // Subquery từ bảng Product_Images (is_main = 1)

    Long getTotalStock(); // SUM(stock_quantity) từ Product_Variants

    Integer getStatus();

    LocalDateTime getCreatedAt();
}
