package com.shopquanao.bejava.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 4, max = 15, message = "Mã voucher phải từ 4 đến 15 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Mã voucher chỉ được chứa a-z, A-Z, 0-9 và dấu _")
    private String voucherCode;

    @NotNull(message = "Loại giảm giá không được để trống")
    private Integer discountType;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    private BigDecimal discountValue;

    @NotNull(message = "Số lượt sử dụng không được để trống")
    private Integer usageLimit;

    @NotNull(message = "Ngày hết hạn không được để trống")
    private LocalDateTime expiryDate;

    private Integer status;
}
