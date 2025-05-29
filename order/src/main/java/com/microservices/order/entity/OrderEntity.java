package com.microservices.order.entity;

import com.microservices.order.entity.ItemEntity;
import com.microservices.order.model.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "id_customer")
    private Long customerId;

    @Column(name = "cod_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private CreatedUpdatedAtEntity createdUpdatedAt;

    @OneToMany(mappedBy = "order")
    private List<ItemEntity> items;
}
