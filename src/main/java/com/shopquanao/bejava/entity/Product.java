package com.shopquanao.bejava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Entity ánh xạ với bảng "Products" trong database
@Entity
@Table(name = "Products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "slug", nullable = false, unique = true, length = 300)
    private String slug;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship: Product thuộc 1 Category (tương đương virtual Category bên C#)
    // insertable/updatable = false vì đã có field categoryId quản lý cột này
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // Relationship: Product có nhiều Variants
    @OneToMany(mappedBy = "product")
    private List<ProductVariant> variants;

    // Relationship: Product có nhiều Images
    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
