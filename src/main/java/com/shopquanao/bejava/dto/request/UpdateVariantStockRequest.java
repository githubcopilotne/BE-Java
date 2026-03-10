package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// DTO nhận data từ FE khi cộng dồn số lượng tồn kho
@Getter
@Setter
public class UpdateVariantStockRequest {

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity; // Số lượng nhập thêm — BE sẽ cộng vào stock hiện tại
}
