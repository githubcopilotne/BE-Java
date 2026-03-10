package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.dto.request.CreateProductRequest;
import com.shopquanao.bejava.dto.request.CreateVariantRequest;
import com.shopquanao.bejava.dto.request.UpdateProductRequest;
import com.shopquanao.bejava.dto.request.UpdateVariantStockRequest;
import com.shopquanao.bejava.dto.response.UpdateProductResponse;
import com.shopquanao.bejava.entity.Category;
import com.shopquanao.bejava.entity.Product;
import com.shopquanao.bejava.entity.ProductImage;
import com.shopquanao.bejava.entity.ProductVariant;
import com.shopquanao.bejava.repository.CategoryRepository;
import com.shopquanao.bejava.repository.CartItemRepository;
import com.shopquanao.bejava.repository.OrderItemRepository;
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
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository,
            ICloudinaryService cloudinaryService,
            CartItemRepository cartItemRepository,
            OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.cloudinaryService = cloudinaryService;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
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

        // 3. Kiểm tra product đã có ảnh chính chưa
        boolean hasMainImage = productImageRepository.existsByProductIdAndIsMainTrue(productId);

        // Nếu chưa có ảnh chính → bắt buộc chọn mainIndex
        if (!hasMainImage) {
            if (mainIndex == null) {
                return ApiResponse.error("Vui lòng chọn ảnh chính");
            }
            if (mainIndex < 0 || mainIndex >= files.size()) {
                return ApiResponse.error("Vị trí ảnh chính không hợp lệ");
            }
        }

        // 4. Upload từng file lên Cloudinary và lưu vào DB
        List<ProductImage> images = new ArrayList<>();
        try {
            for (int i = 0; i < files.size(); i++) {
                // Upload lên Cloudinary → nhận URL
                String imageUrl = cloudinaryService.upload(files.get(i));

                // Tạo entity
                ProductImage image = new ProductImage();
                image.setProductId(productId);
                image.setImageUrl(imageUrl);

                // Nếu chưa có ảnh chính → ảnh ở mainIndex là main
                // Nếu đã có ảnh chính → tất cả ảnh mới là phụ
                image.setIsMain(!hasMainImage && i == mainIndex);
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

    // Xoá biến thể (hard delete, check FK trước)
    @Override
    public ApiResponse<Void> deleteVariant(Integer productId, Integer variantId) {
        // 1. Tìm variant theo id
        ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
        if (variant == null) {
            return ApiResponse.error("Biến thể không tồn tại");
        }

        // 2. Kiểm tra variant thuộc đúng product
        if (!variant.getProductId().equals(productId)) {
            return ApiResponse.error("Biến thể không thuộc sản phẩm này");
        }

        // 3. Check đã có đơn hàng → không cho xoá
        if (orderItemRepository.existsByVariantId(variantId)) {
            return ApiResponse.error("Không thể xoá biến thể đã có đơn hàng");
        }

        // 4. Xoá cart_items tham chiếu tới variant (nếu có)
        if (cartItemRepository.existsByVariantId(variantId)) {
            cartItemRepository.deleteByVariantId(variantId);
        }

        // 5. Hard delete variant
        productVariantRepository.deleteById(variantId);

        return ApiResponse.success(null, "Xoá biến thể thành công");
    }

    // Toggle trạng thái ẩn/hiện sản phẩm
    @Override
    public ApiResponse<Map<String, Integer>> toggleProductStatus(Integer productId) {
        // 1. Tìm product
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ApiResponse.error("Sản phẩm không tồn tại");
        }

        // 2. Nếu đang ẩn (0) → muốn bật hiện (1) → check điều kiện
        if (product.getStatus() == 0) {
            // Check category đang hiện
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            if (category == null || category.getStatus() != 1) {
                return ApiResponse.error("Danh mục đang ẩn, không thể đăng bán");
            }

            // Check có ít nhất 1 variant
            if (!productVariantRepository.existsByProductId(productId)) {
                return ApiResponse.error("Cần ít nhất 1 biến thể để đăng bán");
            }

            // Check có ảnh chính
            if (!productImageRepository.existsByProductIdAndIsMainTrue(productId)) {
                return ApiResponse.error("Cần có ảnh chính để đăng bán");
            }

            product.setStatus(1);
        } else {
            // 3. Đang hiện (1) → tắt luôn, không cần check
            product.setStatus(0);
        }

        productRepository.save(product);

        return ApiResponse.success(Map.of("status", product.getStatus()), "Cập nhật trạng thái thành công");
    }

    // Đổi ảnh chính
    @Override
    public ApiResponse<Void> setMainImage(Integer productId, Integer imageId) {
        // 1. Tìm image theo id
        ProductImage image = productImageRepository.findById(imageId).orElse(null);
        if (image == null) {
            return ApiResponse.error("Ảnh không tồn tại");
        }

        // 2. Check image thuộc đúng product
        if (!image.getProductId().equals(productId)) {
            return ApiResponse.error("Ảnh không thuộc sản phẩm này");
        }

        // 3. Check image đã là ảnh chính chưa
        if (image.getIsMain()) {
            return ApiResponse.error("Ảnh này đã là ảnh chính");
        }

        // 4. Bỏ isMain ảnh cũ
        ProductImage currentMain = productImageRepository.findByProductIdAndIsMainTrue(productId);
        if (currentMain != null) {
            currentMain.setIsMain(false);
            productImageRepository.save(currentMain);
        }

        // 5. Set isMain ảnh mới
        image.setIsMain(true);
        productImageRepository.save(image);

        return ApiResponse.success(null, "Đổi ảnh chính thành công");
    }
}
