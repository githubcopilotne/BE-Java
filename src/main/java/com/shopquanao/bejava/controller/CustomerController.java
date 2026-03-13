package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CustomerDetailProjection;
import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import com.shopquanao.bejava.service.interfaces.ICustomerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // Admin + Staff đều được xem danh sách khách hàng
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Staff')")
    public ResponseEntity<ApiResponse<Page<CustomerListProjection>>> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var response = customerService.getCustomers(keyword, status, page, size);
        return ResponseEntity.ok(response);
    }

    // Admin + Staff đều được xem chi tiết khách hàng
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Staff')")
    public ResponseEntity<ApiResponse<CustomerDetailProjection>> getCustomerById(@PathVariable Integer id) {
        var response = customerService.getCustomerById(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Chỉ Admin được khóa/mở khóa tài khoản khách hàng
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> toggleCustomerStatus(@PathVariable Integer id) {
        var response = customerService.toggleCustomerStatus(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
