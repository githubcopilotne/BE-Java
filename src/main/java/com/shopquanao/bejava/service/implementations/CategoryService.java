package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CategoryListProjection;
import com.shopquanao.bejava.dto.request.CreateCategoryRequest;
import com.shopquanao.bejava.dto.request.UpdateCategoryRequest;
import com.shopquanao.bejava.entity.Category;
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.service.interfaces.ICategoryService;
import com.shopquanao.bejava.util.SlugUtils;
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

    // Tạo mới danh mục
    @Override
    public ApiResponse<Category> createCategory(CreateCategoryRequest request) {
        // 1. Trim input
        String categoryName = request.getCategoryName().trim();
        String description = request.getDescription() != null ? request.getDescription().trim() : null;

        // 2. Kiểm tra trùng tên
        if (categoryRepository.existsByCategoryName(categoryName)) {
            return ApiResponse.error("Tên danh mục đã tồn tại");
        }

        // 3. Validate status (chỉ chấp nhận 0 hoặc 1)
        Integer status = request.getStatus();
        if (status != null && status != 0 && status != 1) {
            return ApiResponse.error("Trạng thái không hợp lệ");
        }

        // 4. Tạo entity và set giá trị
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setSlug(SlugUtils.toSlug(categoryName));
        category.setDescription(description);
        category.setStatus(status != null ? status : 1); // Mặc định = 1 (hiện)

        // 5. Lưu vào DB (createdAt tự set bởi @PrePersist)
        Category saved = categoryRepository.save(category);

        return ApiResponse.success(saved, "Tạo danh mục thành công");
    }

    // Cập nhật danh mục
    @Override
    public ApiResponse<Category> updateCategory(Integer id, UpdateCategoryRequest request) {
        // 1. Tìm category theo id
        var optional = categoryRepository.findById(id);
        if (optional.isEmpty()) {
            return ApiResponse.error("Danh mục không tồn tại");
        }
        Category category = optional.get();

        // 2. Trim input
        String categoryName = request.getCategoryName().trim();
        String description = request.getDescription() != null ? request.getDescription().trim() : null;

        // 3. Kiểm tra trùng tên (loại trừ chính nó)
        if (categoryRepository.existsByCategoryNameAndCategoryIdNot(categoryName, id)) {
            return ApiResponse.error("Tên danh mục đã tồn tại");
        }

        // 4. Validate status (chỉ chấp nhận 0 hoặc 1)
        if (request.getStatus() != 0 && request.getStatus() != 1) {
            return ApiResponse.error("Trạng thái không hợp lệ");
        }

        // 5. Cập nhật giá trị
        category.setCategoryName(categoryName);
        category.setSlug(SlugUtils.toSlug(categoryName)); // Sinh lại slug nếu đổi tên
        category.setDescription(description);
        category.setStatus(request.getStatus());

        // 6. Lưu vào DB (updatedAt tự set bởi @PreUpdate)
        Category saved = categoryRepository.save(category);

        return ApiResponse.success(saved, "Cập nhật danh mục thành công");
    }

    // Lấy chi tiết danh mục theo id
    @Override
    public ApiResponse<Category> getCategoryById(Integer id) {
        var optional = categoryRepository.findById(id);
        if (optional.isEmpty()) {
            return ApiResponse.error("Danh mục không tồn tại");
        }

        return ApiResponse.success(optional.get(), "Lấy danh mục thành công");
    }
}
