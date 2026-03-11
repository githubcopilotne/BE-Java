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

    // Xoá ảnh trên Cloudinary theo URL
    @Override
    public void delete(String imageUrl) throws IOException {
        // Trích xuất public_id từ URL
        // URL dạng: https://res.cloudinary.com/.../upload/v123/products/abc123.jpg
        // public_id = "products/abc123"
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    // Trích xuất public_id từ Cloudinary URL
    private String extractPublicId(String imageUrl) {
        // Tìm vị trí "/upload/" → lấy phần sau đó → bỏ version (v123/) → bỏ extension
        // (.jpg)
        String afterUpload = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
        // Bỏ version: "v123/products/abc123.jpg" → "products/abc123.jpg"
        if (afterUpload.startsWith("v")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }
        // Bỏ extension: "products/abc123.jpg" → "products/abc123"
        return afterUpload.substring(0, afterUpload.lastIndexOf("."));
    }
}
