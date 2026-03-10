package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.dto.request.CreateProductRequest;
import com.shopquanao.bejava.dto.request.CreateVariantRequest;
import com.shopquanao.bejava.dto.request.UpdateProductRequest;
import com.shopquanao.bejava.dto.request.UpdateVariantStockRequest;
import com.shopquanao.bejava.dto.response.UpdateProductResponse;
import com.shopquanao.bejava.entity.Product;
import com.shopquanao.bejava.entity.ProductImage;
import com.shopquanao.bejava.entity.ProductVariant;
import com.shopquanao.bejava.service.interfaces.IProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    // POST /api/products — Tạo mới sản phẩm
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        var response = productService.createProduct(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // POST /api/products/{id}/variants — Thêm biến thể cho sản phẩm
    @PostMapping("/{id}/variants")
    public ResponseEntity<ApiResponse<List<ProductVariant>>> addVariants(
            @PathVariable Integer id,
            @Valid @RequestBody List<CreateVariantRequest> requests) {
        var response = productService.addVariants(id, requests);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // POST /api/products/{id}/images — Upload ảnh cho sản phẩm
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<ProductImage>>> addImages(
            @PathVariable Integer id,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "mainIndex", required = false) Integer mainIndex) {
        var response = productService.addImages(id, files, mainIndex);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // GET /api/products/{id} — Xem chi tiết sản phẩm
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Integer id) {
        var response = productService.getProductById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // PUT /api/products/{id} — Cập nhật thông tin chung sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        var response = productService.updateProduct(id, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // PATCH /api/products/{productId}/variants/{variantId}/stock — Cộng dồn số
    // lượng tồn kho
    @PatchMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> updateVariantStock(
            @PathVariable Integer productId,
            @PathVariable Integer variantId,
            @Valid @RequestBody UpdateVariantStockRequest request) {
        var response = productService.updateVariantStock(productId, variantId, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // DELETE /api/products/{productId}/variants/{variantId} — Xoá biến thể
    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable Integer productId,
            @PathVariable Integer variantId) {
        var response = productService.deleteVariant(productId, variantId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}
