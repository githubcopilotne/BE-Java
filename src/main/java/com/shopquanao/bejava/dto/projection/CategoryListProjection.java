package com.shopquanao.bejava.dto.projection;

import java.time.LocalDateTime;

// Interface Projection — Spring tự chỉ lấy 4 field này từ DB
public interface CategoryListProjection {

    Integer getCategoryId();

    String getCategoryName();

    String getDescription();

    LocalDateTime getCreatedAt();
}
