package com.shopquanao.bejava.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// Entity ánh xạ với bảng "Order_Items" trong database
@Entity
@Table(name = "Order_Items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "variant_id", nullable = false)
    private Integer variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "total_money", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;
}
