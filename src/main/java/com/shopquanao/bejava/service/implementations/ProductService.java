package com.shopquanao.bejava.service.implementations;

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
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.repository.ProductImageRepository;
import com.shopquanao.bejava.repository.ProductRepository;
import com.shopquanao.bejava.repository.ProductVariantRepository;
import com.shopquanao.bejava.service.interfaces.ICloudinaryService;
import com.shopquanao.bejava.service.interfaces.IProductService;
import com.shopquanao.bejava.util.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ICloudinaryService cloudinaryService;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository,
            ICloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.cloudinaryService = cloudinaryService;
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

    // Thêm hình ảnh cho sản phẩm
    @Override
    public ApiResponse<List<ProductImage>> addImages(Integer productId, List<MultipartFile> files, Integer mainIndex) {
        // 1. Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            return ApiResponse.error("Sản phẩm không tồn tại");
        }

        // 2. Kiểm tra có file ảnh không
        if (files == null || files.isEmpty()) {
            return ApiResponse.error("Vui lòng chọn ít nhất 1 ảnh");
        }

        // 2.1. Kiểm tra file không rỗng (Postman có thể gửi key nhưng không chọn file
        // thực tế)
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return ApiResponse.error("File ảnh không được rỗng");
            }
        }

        // 3. Validate mainIndex — admin phải chọn ảnh main
        if (mainIndex == null) {
            return ApiResponse.error("Vui lòng chọn ảnh chính");
        }
        if (mainIndex < 0 || mainIndex >= files.size()) {
            return ApiResponse.error("Vị trí ảnh chính không hợp lệ");
        }

        // 4. Upload từng file lên Cloudinary và lưu vào DB
        List<ProductImage> images = new ArrayList<>();
        try {
            for (int i = 0; i < files.size(); i++) {
                // Upload lên Cloudinary → nhận URL
                String imageUrl = cloudinaryService.upload(files.get(i));

                // Tạo entity — ảnh ở vị trí mainIndex thì isMain = true
                ProductImage image = new ProductImage();
                image.setProductId(productId);
                image.setImageUrl(imageUrl);
                image.setIsMain(i == mainIndex); // Admin chọn ảnh nào là main
                images.add(image);
            }
        } catch (IOException | RuntimeException e) {
            return ApiResponse.error("Upload ảnh thất bại: " + e.getMessage());
        }

        // 5. Lưu tất cả images vào DB
        List<ProductImage> saved = productImageRepository.saveAll(images);

        return ApiResponse.success(saved, "Thêm ảnh sản phẩm thành công");
    }

    // Xem chi tiết sản phẩm — trả entity kèm category, variants, images nhờ
    // relationship
    @Override
    public ApiResponse<Product> getProductById(Integer productId) {
        // Tìm product theo id — relationship tự load category, variants, images
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ApiResponse.error("Sản phẩm không tồn tại");
        }

        return ApiResponse.success(product, "Lấy chi tiết sản phẩm thành công");
    }

    // Cập nhật thông tin chung sản phẩm
    @Override
    public ApiResponse<UpdateProductResponse> updateProduct(Integer productId, UpdateProductRequest request) {
        // 1. Tìm sản phẩm theo id
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ApiResponse.error("Sản phẩm không tồn tại");
        }

        // 2. Trim input
        String productName = request.getProductName().trim();
        String description = request.getDescription() != null ? request.getDescription().trim() : null;

        // 3. Kiểm tra danh mục tồn tại
        if (!categoryRepository.existsById(request.getCategoryId())) {
            return ApiResponse.error("Danh mục không tồn tại");
        }

        // 4. Kiểm tra trùng tên — loại trừ chính sản phẩm đang sửa
        if (productRepository.existsByProductNameAndProductIdNot(productName, productId)) {
            return ApiResponse.error("Tên sản phẩm đã tồn tại");
        }

        // 5. Cập nhật các field
        product.setProductName(productName);
        product.setCategoryId(request.getCategoryId());
        product.setUnitPrice(request.getUnitPrice());
        product.setDescription(description);
        product.setSlug(SlugUtils.toSlug(productName)); // Cập nhật slug theo tên mới

        // 6. Lưu vào DB (updatedAt tự set bởi @PreUpdate)
        Product saved = productRepository.save(product);

        // 7. Map sang DTO — chỉ trả field mà FE chưa có
        UpdateProductResponse dto = new UpdateProductResponse();
        dto.setSlug(saved.getSlug());
        dto.setUpdatedAt(saved.getUpdatedAt());

        return ApiResponse.success(dto, "Cập nhật sản phẩm thành công");
    }

    // Cập nhật số lượng tồn kho cho biến thể (cộng dồn hoặc ghi đè)
    @Override
    public ApiResponse<Map<String, Integer>> updateVariantStock(Integer productId, Integer variantId,
            UpdateVariantStockRequest request) {
        // 1. Tìm variant theo id
        ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
        if (variant == null) {
            return ApiResponse.error("Biến thể không tồn tại");
        }

        // 2. Kiểm tra variant thuộc đúng product
        if (!variant.getProductId().equals(productId)) {
            return ApiResponse.error("Biến thể không thuộc sản phẩm này");
        }

        // 3. Xử lý theo mode
        String mode = request.getMode().trim().toLowerCase();
        int newStock;

        if ("add".equals(mode)) {
            // Cộng dồn — quantity phải >= 1
            if (request.getQuantity() < 1) {
                return ApiResponse.error("Số lượng nhập thêm phải lớn hơn 0");
            }
            newStock = variant.getStockQuantity() + request.getQuantity();
        } else if ("set".equals(mode)) {
            // Ghi đè — quantity phải >= 0
            if (request.getQuantity() < 0) {
                return ApiResponse.error("Số lượng không được âm");
            }
            newStock = request.getQuantity();
        } else {
            return ApiResponse.error("Mode không hợp lệ (chỉ chấp nhận 'add' hoặc 'set')");
        }

        // 4. Lưu vào DB
        variant.setStockQuantity(newStock);
        productVariantRepository.save(variant);

        // 5. Trả stockQuantity mới cho FE cập nhật
        return ApiResponse.success(Map.of("stockQuantity", newStock), "Cập nhật số lượng thành công");
    }
}
