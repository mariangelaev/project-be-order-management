package com.microservices.order.adapter;

import com.microservices.order.entity.OrderEntity;
import com.microservices.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderAdapter {

    private final ItemAdapter itemAdapter;

    public Order adapt(OrderEntity entity) {
        Order order = new Order();
        order.setOrderId(entity.getOrderId());
        order.setCreatedAt(entity.getCreatedUpdatedAt().getCreatedAt());
        order.setUpdatedAt(entity.getCreatedUpdatedAt().getUpdatedAt());
        order.setOrderStatus(entity.getStatus());
        order.setCustomerId(entity.getCustomerId());

        order.setItems(entity.getItems()
                .stream()
                .map(itemAdapter::adapt)
                .collect(Collectors.toList()));
        return order;
    }
}
