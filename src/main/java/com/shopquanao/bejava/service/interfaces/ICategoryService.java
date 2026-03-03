package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;

import java.util.List;

public interface ICategoryService {

    ApiResponse<List<CategoryListProjection>> getAllCategories();
}
