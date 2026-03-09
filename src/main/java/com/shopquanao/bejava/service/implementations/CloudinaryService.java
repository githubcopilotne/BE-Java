package com.shopquanao.bejava.service.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shopquanao.bejava.service.interfaces.ICloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// Service xử lý upload ảnh lên Cloudinary
@Service
public class CloudinaryService implements ICloudinaryService {

    private final Cloudinary cloudinary;

    // Đọc 3 key từ application.properties để khởi tạo kết nối Cloudinary
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Upload 1 file ảnh lên Cloudinary, trả về URL
    @Override
    @SuppressWarnings("unchecked")
    public String upload(MultipartFile file) throws IOException {
        // Upload vào folder "products" trên Cloudinary để dễ quản lý
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "products"));

        // Cloudinary trả về nhiều thông tin, chỉ lấy URL
        return result.get("secure_url").toString();
    }
}
