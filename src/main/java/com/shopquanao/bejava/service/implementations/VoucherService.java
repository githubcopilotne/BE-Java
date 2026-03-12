package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.dto.request.CreateVoucherRequest;
import com.shopquanao.bejava.dto.request.UpdateVoucherRequest;
import com.shopquanao.bejava.dto.response.VoucherStatsResponse;
import com.shopquanao.bejava.entity.Voucher;
import com.shopquanao.bejava.repository.VoucherRepository;
import com.shopquanao.bejava.service.interfaces.IVoucherService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherService implements IVoucherService {

    private static final int DISCOUNT_TYPE_PERCENT = 1;
    private static final int DISCOUNT_TYPE_AMOUNT = 2;

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public ApiResponse<List<VoucherListProjection>> getAllVouchers() {
        var vouchers = voucherRepository.findAllBy();
        return ApiResponse.success(vouchers, "Lấy danh sách voucher thành công");
    }

    @Override
    public ApiResponse<Voucher> createVoucher(CreateVoucherRequest request) {
        String voucherCode = request.getVoucherCode().trim();

        if (voucherRepository.existsByVoucherCode(voucherCode)) {
            return ApiResponse.error("Mã voucher đã tồn tại");
        }

        Integer discountType = request.getDiscountType();
        BigDecimal discountValue = request.getDiscountValue();

        if (discountType == null
                || (discountType != DISCOUNT_TYPE_PERCENT && discountType != DISCOUNT_TYPE_AMOUNT)) {
            return ApiResponse.error("Loại giảm giá không hợp lệ");
        }

        if (discountType == DISCOUNT_TYPE_PERCENT) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0
                    || discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                return ApiResponse.error("Giá trị giảm theo phần trăm phải > 0 và <= 100");
            }
        }

        if (discountType == DISCOUNT_TYPE_AMOUNT) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("Giá trị giảm theo tiền phải > 0");
            }
        }

        if (request.getUsageLimit() <= 0) {
            return ApiResponse.error("Số lượt sử dụng phải > 0");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!request.getExpiryDate().isAfter(now)) {
            return ApiResponse.error("Ngày hết hạn phải lớn hơn ngày hiện tại");
        }

        Integer status = request.getStatus();
        if (status != null && status != 0 && status != 1) {
            return ApiResponse.error("Trạng thái không hợp lệ");
        }

        Voucher voucher = new Voucher();
        voucher.setVoucherCode(voucherCode);
        voucher.setDiscountType(discountType);
        voucher.setDiscountValue(discountValue);
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsedCount(0);
        voucher.setExpiryDate(request.getExpiryDate());
        voucher.setStatus(status != null ? status : 1);
        voucher.setCreatedAt(now);

        Voucher saved = voucherRepository.save(voucher);
        return ApiResponse.success(saved, "Tạo voucher thành công");
    }

    @Override
    public ApiResponse<Voucher> getVoucherById(Integer id) {
        var voucher = voucherRepository.findById(id);
        if (voucher.isEmpty()) {
            return ApiResponse.error("Voucher không tồn tại");
        }
        return ApiResponse.success(voucher.get(), "Lấy voucher chi tiết thành công");
    }

    @Override
    public ApiResponse<Voucher> updateVoucher(Integer id, UpdateVoucherRequest request) {
        var optional = voucherRepository.findById(id);
        if (optional.isEmpty()) {
            return ApiResponse.error("Voucher không tồn tại");
        }

        Voucher voucher = optional.get();
        String voucherCode = request.getVoucherCode().trim();
        Integer discountType = request.getDiscountType();
        BigDecimal discountValue = request.getDiscountValue();
        Integer usageLimit = request.getUsageLimit();
        LocalDateTime expiryDate = request.getExpiryDate();
        Integer status = request.getStatus();
        Integer usedCount = voucher.getUsedCount() != null ? voucher.getUsedCount() : 0;

        if (discountType != DISCOUNT_TYPE_PERCENT && discountType != DISCOUNT_TYPE_AMOUNT) {
            return ApiResponse.error("Loại giảm giá không hợp lệ");
        }

        if (discountType == DISCOUNT_TYPE_PERCENT) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0
                    || discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                return ApiResponse.error("Giá trị giảm theo phần trăm phải > 0 và <= 100");
            }
        }

        if (discountType == DISCOUNT_TYPE_AMOUNT) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("Giá trị giảm theo tiền phải > 0");
            }
        }

        if (usageLimit <= 0) {
            return ApiResponse.error("Số lượt sử dụng phải > 0");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!expiryDate.isAfter(now)) {
            return ApiResponse.error("Ngày hết hạn phải lớn hơn ngày hiện tại");
        }

        if (status != 0 && status != 1) {
            return ApiResponse.error("Trạng thái không hợp lệ");
        }

        if (usedCount == 0) {
            if (voucherRepository.existsByVoucherCodeAndVoucherIdNot(voucherCode, id)) {
                return ApiResponse.error("Mã voucher đã tồn tại");
            }
        } else {
            if (!voucher.getVoucherCode().equals(voucherCode)) {
                return ApiResponse.error("Voucher đã có lượt sử dụng, không được sửa mã voucher");
            }

            if (!voucher.getDiscountType().equals(discountType)) {
                return ApiResponse.error("Voucher đã có lượt sử dụng, không được sửa loại giảm giá");
            }

            if (voucher.getDiscountValue().compareTo(discountValue) != 0) {
                return ApiResponse.error("Voucher đã có lượt sử dụng, không được sửa giá trị giảm");
            }

            if (usageLimit < usedCount) {
                return ApiResponse.error("Không được giảm usage limit nhỏ hơn số lượt đã sử dụng");
            }
        }

        if (usedCount == 0) {
            voucher.setVoucherCode(voucherCode);
            voucher.setDiscountType(discountType);
            voucher.setDiscountValue(discountValue);
        }

        voucher.setUsageLimit(usageLimit);
        voucher.setExpiryDate(expiryDate);
        voucher.setStatus(status);

        Voucher saved = voucherRepository.save(voucher);
        return ApiResponse.success(saved, "Cập nhật voucher thành công");
    }

    @Override
    public ApiResponse<Voucher> deleteVoucher(Integer id) {
        var optional = voucherRepository.findById(id);
        if (optional.isEmpty()) {
            return ApiResponse.error("Voucher không tồn tại");
        }

        Voucher voucher = optional.get();
        int usedCount = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
        if (usedCount > 0) {
            return ApiResponse.error("Voucher đã có lượt sử dụng, không được xóa");
        }

        voucherRepository.delete(voucher);
        return ApiResponse.success(voucher, "Xóa voucher thành công");
    }

    @Override
    public ApiResponse<VoucherStatsResponse> getStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(7);

        var stats = new VoucherStatsResponse(
                voucherRepository.count(),
                voucherRepository.countActive(now),
                voucherRepository.countNearExpiry(now, end),
                voucherRepository.countExpired(now));

        return ApiResponse.success(stats, "Lấy thống kê voucher thành công");
    }
}
