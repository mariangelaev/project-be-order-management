package com.microservices.order.adapter;

import com.microservices.order.entity.CreatedUpdatedAtEntity;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.entity.OrderEntity;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderAdapterTest {

    private final ItemAdapter itemAdapter = Mockito.mock(ItemAdapter.class);
    private OrderAdapter orderAdapter;

    @Before
    public void setUp() {
        this.orderAdapter = new OrderAdapter(itemAdapter);
    }

    @Test
    public void toModelTest() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CONFIRMED);
        orderEntity.setOrderId(1L);
        orderEntity.setCustomerId(5L);
        CreatedUpdatedAtEntity createdUpdatedAt = new CreatedUpdatedAtEntity();
        createdUpdatedAt.setCreatedAt(LocalDateTime.now());
        createdUpdatedAt.setUpdatedAt(LocalDateTime.now());
        orderEntity.setCreatedUpdatedAt(createdUpdatedAt);
        orderEntity.setItems(List.of(new ItemEntity()));

        Order order = orderAdapter.adapt(orderEntity);
        Assert.assertEquals(orderEntity.getStatus(), order.getOrderStatus());
        Assert.assertEquals(orderEntity.getOrderId(), order.getOrderId());
        Assert.assertEquals(orderEntity.getCustomerId(), order.getCustomerId());
        Assert.assertEquals(orderEntity.getCreatedUpdatedAt().getCreatedAt(), order.getCreatedAt());
        Assert.assertEquals(orderEntity.getCreatedUpdatedAt().getUpdatedAt(), order.getUpdatedAt());
        verify(itemAdapter, times(1)).adapt(any());
    }
}
