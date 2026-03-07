package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.VoucherListProjection;
import com.shopquanao.bejava.dto.request.CreateVoucherRequest;
import com.shopquanao.bejava.dto.request.UpdateVoucherRequest;
import com.shopquanao.bejava.entity.Voucher;
import com.shopquanao.bejava.service.interfaces.IVoucherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@PreAuthorize("hasRole('Admin')")
public class VoucherController {

    private final IVoucherService voucherService;

    public VoucherController(IVoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherListProjection>>> getAllVouchers() {
        var response = voucherService.getAllVouchers();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Voucher>> createVoucher(
            @Valid @RequestBody CreateVoucherRequest request) {
        var response = voucherService.createVoucher(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Voucher>> getVoucherById(@PathVariable Integer id) {
        var response = voucherService.getVoucherById(id);
        if (response.isSuccess())
            return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Voucher>> updateVoucher(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateVoucherRequest request) {
        var response = voucherService.updateVoucher(id, request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Voucher>> deleteVoucher(@PathVariable Integer id) {
        var response = voucherService.deleteVoucher(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
