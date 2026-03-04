package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO nhận data từ FE khi tạo danh mục mới
@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
    private String categoryName;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description; // Tuỳ chọn — null OK

    private Integer status; // Tuỳ chọn — null thì mặc định = 1
}
