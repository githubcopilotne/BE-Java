package com.shopquanao.bejava.exception;

import com.shopquanao.bejava.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Bắt exception toàn cục — trả về đúng format ApiResponse
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi validation (@NotBlank, @Size...) từ @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        // Lấy message lỗi đầu tiên
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
}
