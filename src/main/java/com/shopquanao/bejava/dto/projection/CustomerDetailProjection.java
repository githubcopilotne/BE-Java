package com.shopquanao.bejava.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CustomerDetailProjection {

    Integer getUserId();

    String getFullName();

    String getEmail();

    String getPhone();

    Integer getGender();

    LocalDate getBirthday();

    String getAddress();

    LocalDateTime getCreatedAt();

    Integer getStatus();

    // Thống kê từ bảng Orders (LEFT JOIN)
    Integer getTotalOrders();

    BigDecimal getTotalSpent();

}
