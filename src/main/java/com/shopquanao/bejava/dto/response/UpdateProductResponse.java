package com.shopquanao.bejava.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// DTO response cho API cập nhật thông tin chung — chỉ trả field mà FE chưa có
@Getter
@Setter
public class UpdateProductResponse {

    private String slug; // Backend tự sinh từ tên mới
    private LocalDateTime updatedAt; // Backend tự set bởi @PreUpdate
}
