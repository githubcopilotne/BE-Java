package com.shopquanao.bejava.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

// Utility class — sinh slug từ chuỗi tiếng Việt
// Ví dụ: "Áo Thun Nam" → "ao-thun-nam"
public class SlugUtils {

    private SlugUtils() {
        // Không cho tạo instance — chỉ dùng static method
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String slug = input.trim();

        // Bước 1: Chuyển về chữ thường
        slug = slug.toLowerCase();

        // Bước 2: Bỏ dấu tiếng Việt (Normalize → loại bỏ combining marks)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(slug).replaceAll("");

        // Bước 3: Xử lý ký tự đặc biệt tiếng Việt (đ → d)
        slug = slug.replace("đ", "d");

        // Bước 4: Thay khoảng trắng và ký tự đặc biệt bằng dấu gạch ngang
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");  // Giữ lại chữ, số, space, gạch ngang
        slug = slug.replaceAll("[\\s-]+", "-");         // Gộp space + gạch ngang liên tiếp thành 1
        slug = slug.replaceAll("^-|-$", "");            // Bỏ gạch ngang đầu/cuối

        return slug;
    }
}
