package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.service.interfaces.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy tất cả danh mục cho admin list
    @Override
    public ApiResponse<List<CategoryListProjection>> getAllCategories() {
        var categories = categoryRepository.findAllBy();
        return ApiResponse.success(categories, "Lấy danh sách danh mục thành công");
    }
}
