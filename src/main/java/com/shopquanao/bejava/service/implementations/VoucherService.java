package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.repository.VoucherRepository;
import com.shopquanao.bejava.service.interfaces.IVoucherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService implements IVoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public ApiResponse<List<VoucherListProjection>> getAllVouchers() {
        var vouchers = voucherRepository.findAllBy();
        return ApiResponse.success(vouchers, "Lấy danh sách voucher thành công");
    }
}
