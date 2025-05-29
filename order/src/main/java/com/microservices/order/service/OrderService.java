package com.microservices.order.service;

import com.microservices.order.adapter.OrderAdapter;
import com.microservices.order.entity.CreatedUpdatedAtEntity;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.entity.OrderEntity;
import com.microservices.order.exception.OrderNotFoundRuntimeException;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.repository.ItemRepository;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderAdapter orderAdapter;

    public Order createOrder(Long customerId, List<ProductQuantity> productQuantities) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomerId(customerId);
        orderEntity.setStatus(OrderStatus.PROCESSING);
        orderEntity.setCreatedUpdatedAt(new CreatedUpdatedAtEntity());
        orderEntity = orderRepository.save(orderEntity);

        List<ItemEntity> items = new ArrayList<>();
        for (ProductQuantity productQuantity : productQuantities) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setProductId(productQuantity.getProductId());
            itemEntity.setQuantity(productQuantity.getQuantity());
            itemEntity.setOrder(orderEntity);
            itemEntity.setCreatedUpdatedAt(new CreatedUpdatedAtEntity());
            items.add(itemRepository.save(itemEntity));
        }
        orderEntity.setItems(items);
        return orderAdapter.adapt(orderRepository.save(orderEntity));
    }

    public Order getOrder(Long orderId) {
        Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(orderId);
        if (optionalOrderEntity.isEmpty()) {
            throw new OrderNotFoundRuntimeException(orderId);
        }
        return orderAdapter.adapt(optionalOrderEntity.get());
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(orderId);
        if (optionalOrderEntity.isEmpty()) {
            throw new OrderNotFoundRuntimeException(orderId);
        }
        OrderEntity orderEntity = optionalOrderEntity.get();
        orderEntity.setStatus(status);
        return orderAdapter.adapt(orderRepository.save(orderEntity));
    }
}