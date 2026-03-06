package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.VoucherListProjection;

import java.util.List;

public interface IVoucherService {

    ApiResponse<List<VoucherListProjection>> getAllVouchers();
}
