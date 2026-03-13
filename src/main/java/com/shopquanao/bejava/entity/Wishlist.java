package com.shopquanao.bejava.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entity cho bảng Wishlists — danh sách yêu thích
@Entity
@Table(name = "Wishlists")
@IdClass(WishlistId.class)
public class Wishlist {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
