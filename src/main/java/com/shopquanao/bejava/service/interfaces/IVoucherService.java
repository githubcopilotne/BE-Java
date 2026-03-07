package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.dto.request.CreateVoucherRequest;
import com.shopquanao.bejava.dto.request.UpdateVoucherRequest;
import com.shopquanao.bejava.entity.Voucher;

import java.util.List;

public interface IVoucherService {

    ApiResponse<List<VoucherListProjection>> getAllVouchers();

    ApiResponse<Voucher> createVoucher(CreateVoucherRequest request);

    ApiResponse<Voucher> getVoucherById(Integer id);

    ApiResponse<Voucher> updateVoucher(Integer id, UpdateVoucherRequest request);

}
