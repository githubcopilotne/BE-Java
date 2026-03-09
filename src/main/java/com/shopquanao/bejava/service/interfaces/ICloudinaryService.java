package com.shopquanao.bejava.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Interface cho service upload ảnh lên Cloudinary
public interface ICloudinaryService {

    // Upload 1 file ảnh, trả về URL
    String upload(MultipartFile file) throws IOException;
}
