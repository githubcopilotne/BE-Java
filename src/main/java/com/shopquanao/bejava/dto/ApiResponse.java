package com.shopquanao.bejava.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Response wrapper cho tất cả API — format thống nhất: { success, message, data }
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn field nào null thì không trả về FE
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Tạo response thành công
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    // Tạo response lỗi
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
