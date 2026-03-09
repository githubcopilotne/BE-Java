package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO nhận data từ FE khi thêm biến thể cho sản phẩm
@Getter
@Setter
public class CreateVariantRequest {

    @NotBlank(message = "Màu sắc không được để trống")
    @Size(max = 50, message = "Màu sắc không được vượt quá 50 ký tự")
    private String color;

    @NotBlank(message = "Kích cỡ không được để trống")
    @Size(max = 10, message = "Kích cỡ không được vượt quá 10 ký tự")
    private String size;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity;
}
