package com.shopquanao.bejava.repository;

import com.shopquanao.bejava.entity.Wishlist;
import com.shopquanao.bejava.entity.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository truy vấn bảng Wishlists
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    // Xoá tất cả wishlist của 1 product
    void deleteByProductId(Integer productId);
}
