package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.ProductListProjection;
import com.shopquanao.bejava.repository.ProductRepository;
import com.shopquanao.bejava.service.interfaces.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lấy danh sách sản phẩm có phân trang
    @Override
    public ApiResponse<Page<ProductListProjection>> getAllProducts(Pageable pageable) {
        var products = productRepository.findAllProducts(pageable);
        return ApiResponse.success(products, "Lấy danh sách sản phẩm thành công");
    }
}
