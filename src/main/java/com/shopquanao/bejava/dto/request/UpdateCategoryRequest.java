package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO nhận data từ FE khi cập nhật danh mục
@Getter
@Setter
public class UpdateCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
    private String categoryName;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description; // Tuỳ chọn — null OK

    @NotNull(message = "Trạng thái không được để trống")
    private Integer status; // Bắt buộc — admin phải chọn ẩn/hiện
}
