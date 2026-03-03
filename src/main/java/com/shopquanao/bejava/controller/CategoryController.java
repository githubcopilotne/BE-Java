package com.shopquanao.bejava.controller;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.service.interfaces.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller xử lý API danh mục
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories — Lấy danh sách danh mục (chỉ Admin)
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<List<CategoryListProjection>>> getAllCategories() {
        var response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }
}
