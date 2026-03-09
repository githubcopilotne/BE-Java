package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.dto.request.CreateProductRequest;
import com.shopquanao.bejava.entity.Product;
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.repository.ProductRepository;
import com.shopquanao.bejava.service.interfaces.IProductService;
import com.shopquanao.bejava.util.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Lấy danh sách sản phẩm có phân trang
    @Override
    public ApiResponse<Page<ProductListProjection>> getAllProducts(Pageable pageable) {
        var products = productRepository.findAllProducts(pageable);
        return ApiResponse.success(products, "Lấy danh sách sản phẩm thành công");
    }

    // Tạo mới sản phẩm
    @Override
    public ApiResponse<Product> createProduct(CreateProductRequest request) {
        // 1. Trim input
        String productName = request.getProductName().trim();
        String description = request.getDescription() != null ? request.getDescription().trim() : null;

        // 2. Kiểm tra danh mục tồn tại
        if (!categoryRepository.existsById(request.getCategoryId())) {
            return ApiResponse.error("Danh mục không tồn tại");
        }

        // 3. Kiểm tra trùng tên sản phẩm
        if (productRepository.existsByProductName(productName)) {
            return ApiResponse.error("Tên sản phẩm đã tồn tại");
        }

        // 4. Tạo entity và set giá trị
        Product product = new Product();
        product.setProductName(productName);
        product.setCategoryId(request.getCategoryId());
        product.setUnitPrice(request.getUnitPrice());
        product.setDescription(description);
        product.setSlug(SlugUtils.toSlug(productName));
        product.setStatus(0); // Mặc định ẩn — admin cần thêm variant + hình rồi mới hiển thị

        // 5. Lưu vào DB (createdAt tự set bởi @PrePersist)
        Product saved = productRepository.save(product);

        return ApiResponse.success(saved, "Tạo sản phẩm thành công");
    }
}
