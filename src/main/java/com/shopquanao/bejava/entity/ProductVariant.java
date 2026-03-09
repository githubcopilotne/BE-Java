package com.shopquanao.bejava.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Entity ánh xạ với bảng "Product_Variants" trong database
@Entity
@Table(name = "Product_Variants")
@Getter
@Setter
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Integer variantId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    // Relationship: Variant thuộc 1 Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnore
    private Product product;
}
