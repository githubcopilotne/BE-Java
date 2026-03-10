package com.shopquanao.bejava.service.interfaces;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IProductService {

    ApiResponse<Page<ProductListProjection>> getAllProducts(Pageable pageable);

    ApiResponse<Product> createProduct(CreateProductRequest request);

    ApiResponse<List<ProductVariant>> addVariants(Integer productId, List<CreateVariantRequest> requests);

    ApiResponse<List<ProductImage>> addImages(Integer productId, List<MultipartFile> files, Integer mainIndex);

    ApiResponse<Product> getProductById(Integer productId);

    ApiResponse<UpdateProductResponse> updateProduct(Integer productId, UpdateProductRequest request);

    ApiResponse<Map<String, Integer>> updateVariantStock(Integer productId, Integer variantId,
            UpdateVariantStockRequest request);

    ApiResponse<Void> deleteVariant(Integer productId, Integer variantId);
}
