package com.shopquanao.bejava.entity;

import java.io.Serializable;
import java.util.Objects;

// Composite Primary Key cho bảng Wishlists (user_id + product_id)
public class WishlistId implements Serializable {

    private Integer userId;
    private Integer productId;

    public WishlistId() {
    }

    public WishlistId(Integer userId, Integer productId) {
        this.userId = userId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WishlistId that = (WishlistId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }
}
