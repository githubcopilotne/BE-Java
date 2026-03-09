package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.service.interfaces.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Controller xử lý API sản phẩm (chỉ Admin)
@RestController
@RequestMapping("/api/products")
@PreAuthorize("hasRole('Admin')")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    // GET /api/products?page=0&size=10 — Lấy danh sách sản phẩm có phân trang
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductListProjection>>> getAllProducts(Pageable pageable) {
        var response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }
}
