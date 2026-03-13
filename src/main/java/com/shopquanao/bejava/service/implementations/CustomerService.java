package com.shopquanao.bejava.service.implementations;

import com.shopquanao.bejava.dto.ApiResponse;
import com.shopquanao.bejava.dto.projection.CustomerDetailProjection;
import com.shopquanao.bejava.dto.projection.CustomerListProjection;
import com.shopquanao.bejava.entity.User;
import com.shopquanao.bejava.repository.UserRepository;
import com.shopquanao.bejava.service.interfaces.ICustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerService implements ICustomerService {

    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<Page<CustomerListProjection>> getCustomers(String keyword, Integer status, int page, int size) {
        // Trim keyword, nếu rỗng thì set null để query bỏ qua điều kiện tìm kiếm
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        // Validate status: chỉ chấp nhận null, 0, 1
        if (status != null && status != 0 && status != 1) {
            return ApiResponse.error("Trạng thái không hợp lệ (chỉ chấp nhận 0 hoặc 1)");
        }

        // Sắp xếp theo ngày tham gia mới nhất
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CustomerListProjection> customers = userRepository.getCustomers(trimmedKeyword, status, pageable);
        return ApiResponse.success(customers, "Lấy danh sách khách hàng thành công");
    }

    @Override
    public ApiResponse<CustomerDetailProjection> getCustomerById(Integer id) {
        var result = userRepository.getCustomerById(id);

        if (result.isEmpty()) {
            return ApiResponse.error("Khách hàng không tồn tại");
        }

        return ApiResponse.success(result.get(), "Lấy chi tiết khách hàng thành công");
    }

    @Override
    public ApiResponse<Map<String, Integer>> toggleCustomerStatus(Integer id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ApiResponse.error("Khách hàng không tồn tại");
        }

        // Chỉ toggle cho Customer, không cho Staff/Admin
        if (!"Customer".equals(user.getRole())) {
            return ApiResponse.error("Khách hàng không tồn tại");
        }

        int newStatus = (user.getStatus() == 1) ? 0 : 1;
        user.setStatus(newStatus);
        userRepository.save(user);

        String message = (newStatus == 1) ? "Đã mở khóa tài khoản" : "Đã khóa tài khoản";
        return ApiResponse.success(Map.of("status", newStatus), message);
    }
}
