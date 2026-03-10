package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// DTO nhận data từ FE khi cập nhật số lượng tồn kho
@Getter
@Setter
public class UpdateVariantStockRequest {

    @NotBlank(message = "Mode không được để trống")
    private String mode; // "add" = cộng dồn, "set" = ghi đè

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity; // Số lượng nhập thêm hoặc số lượng mới (tuỳ mode)
}
