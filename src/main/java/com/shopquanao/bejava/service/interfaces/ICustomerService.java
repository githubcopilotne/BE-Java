package com.shopquanao.bejava.service.interfaces;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CustomerDetailProjection;
import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import org.springframework.data.domain.Page;

public interface ICustomerService {

    ApiResponse<Page<CustomerListProjection>> getCustomers(String keyword, Integer status, int page, int size);

    ApiResponse<CustomerDetailProjection> getCustomerById(Integer id);

}
