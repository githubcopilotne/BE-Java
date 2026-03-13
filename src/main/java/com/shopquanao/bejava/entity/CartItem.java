package com.shopquanao.bejava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Entity ánh xạ với bảng "Cart_Items" trong database
@Entity
@Table(name = "Cart_Items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @Column(name = "cart_id", nullable = false)
    private Integer cartId;

    @Column(name = "variant_id", nullable = false)
    private Integer variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
