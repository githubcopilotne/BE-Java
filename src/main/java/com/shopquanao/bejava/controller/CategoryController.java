package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.dto.request.CreateCategoryRequest;
import com.shopquanao.bejava.entity.Category;
import com.shopquanao.bejava.service.interfaces.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller xử lý API danh mục (chỉ Admin)
@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('Admin')")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories — Lấy danh sách danh mục
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryListProjection>>> getAllCategories() {
        var response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    // POST /api/categories — Tạo mới danh mục
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        var response = categoryService.createCategory(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}
