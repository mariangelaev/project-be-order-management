package com.microservices.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embedded
    private CreatedUpdatedAtEntity createdUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
