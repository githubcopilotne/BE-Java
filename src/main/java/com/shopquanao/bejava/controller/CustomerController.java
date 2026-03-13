package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import com.shopquanao.bejava.service.interfaces.ICustomerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
