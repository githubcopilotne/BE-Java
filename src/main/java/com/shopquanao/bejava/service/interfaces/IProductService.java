package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.dto.request.CreateProductRequest;
import com.shopquanao.bejava.dto.request.CreateVariantRequest;
import com.shopquanao.bejava.entity.Product;
import com.shopquanao.bejava.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {

    ApiResponse<Page<ProductListProjection>> getAllProducts(Pageable pageable);

    ApiResponse<Product> createProduct(CreateProductRequest request);

    ApiResponse<List<ProductVariant>> addVariants(Integer productId, List<CreateVariantRequest> requests);
}
