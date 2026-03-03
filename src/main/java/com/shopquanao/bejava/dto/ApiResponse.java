package com.shopquanao.bejava.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

// Response wrapper cho tất cả API — format thống nhất: { success, message, data }
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn field nào null thì không trả về FE
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Tạo response thành công
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // Tạo response lỗi
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
