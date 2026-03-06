package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.dto.request.CreateCategoryRequest;
import com.shopquanao.bejava.dto.request.UpdateCategoryRequest;
import com.shopquanao.bejava.entity.Category;

import java.util.List;

public interface ICategoryService {

    ApiResponse<List<CategoryListProjection>> getAllCategories();

    ApiResponse<Category> createCategory(CreateCategoryRequest request);

    ApiResponse<Category> updateCategory(Integer id, UpdateCategoryRequest request);
}
