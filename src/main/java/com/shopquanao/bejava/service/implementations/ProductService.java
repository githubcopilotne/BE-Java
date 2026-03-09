package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.dto.request.CreateProductRequest;
import com.shopquanao.bejava.dto.request.CreateVariantRequest;
import com.shopquanao.bejava.entity.Product;
import com.shopquanao.bejava.entity.ProductVariant;
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.repository.ProductRepository;
import com.shopquanao.bejava.repository.ProductVariantRepository;
import com.shopquanao.bejava.service.interfaces.IProductService;
import com.shopquanao.bejava.util.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productVariantRepository = productVariantRepository;
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

    // Thêm danh sách biến thể cho sản phẩm
    @Override
    public ApiResponse<List<ProductVariant>> addVariants(Integer productId, List<CreateVariantRequest> requests) {
        // 1. Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            return ApiResponse.error("Sản phẩm không tồn tại");
        }

        // 2. Kiểm tra danh sách không rỗng
        if (requests == null || requests.isEmpty()) {
            return ApiResponse.error("Danh sách biến thể không được rỗng");
        }

        // 3. Lấy variants hiện tại của product để check trùng
        var existingVariants = productVariantRepository.findByProductId(productId);

        // 4. Tạo danh sách entity và validate từng variant
        List<ProductVariant> variants = new ArrayList<>();
        for (CreateVariantRequest req : requests) {
            String color = req.getColor().trim();
            String size = req.getSize().trim();

            // Check trùng color + size trong DB
            boolean duplicateInDb = existingVariants.stream()
                    .anyMatch(v -> v.getColor().equalsIgnoreCase(color) && v.getSize().equalsIgnoreCase(size));
            if (duplicateInDb) {
                return ApiResponse.error("Biến thể " + color + " - " + size + " đã tồn tại");
            }

            // Check trùng color + size trong request (trường hợp gửi 2 variant giống nhau)
            boolean duplicateInRequest = variants.stream()
                    .anyMatch(v -> v.getColor().equalsIgnoreCase(color) && v.getSize().equalsIgnoreCase(size));
            if (duplicateInRequest) {
                return ApiResponse.error("Biến thể " + color + " - " + size + " bị trùng trong danh sách");
            }

            ProductVariant variant = new ProductVariant();
            variant.setProductId(productId);
            variant.setColor(color);
            variant.setSize(size);
            variant.setStockQuantity(req.getStockQuantity());
            variants.add(variant);
        }

        // 5. Lưu tất cả variants vào DB
        List<ProductVariant> saved = productVariantRepository.saveAll(variants);

        return ApiResponse.success(saved, "Thêm biến thể thành công");
    }
}
