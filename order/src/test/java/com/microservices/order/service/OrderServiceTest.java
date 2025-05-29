package com.microservices.order.service;

import com.microservices.order.adapter.OrderAdapter;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.entity.OrderEntity;
import com.microservices.order.exception.OrderNotFoundRuntimeException;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.repository.ItemRepository;
import com.microservices.order.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final OrderAdapter orderAdapter = Mockito.mock(OrderAdapter.class);
    private final ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor = ArgumentCaptor.forClass(OrderEntity.class);
    private OrderService orderService;

    @Before
    public void setUp() {
        this.orderService = new OrderService(orderRepository,
                itemRepository,
                orderAdapter);
    }

    @Test
    public void createOrderTest() {
        ProductQuantity productQuantity = new ProductQuantity(1L, 5);
        when(orderRepository.save(any())).thenReturn(new OrderEntity());

        orderService.createOrder(4L, List.of(productQuantity));

        verify(orderRepository, Mockito.times(2)).save(any());
    }

    @Test
    public void getOrderTest_WhenOrderExists() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(new OrderEntity()));
        when(orderAdapter.adapt(any())).thenReturn(new Order());

        Order order = orderService.getOrder(2L);

        assertNotNull(order);
        verify(orderAdapter, times(1)).adapt(any());
    }

    @Test(expected = OrderNotFoundRuntimeException.class)
    public void getOrderTest_WhenOrderDoesNotExists() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        orderService.getOrder(2L);
    }

    @Test
    public void updateOrderStatus_WhenOrderExists() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(new OrderEntity()));

        Order order = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        verify(orderRepository, times(1)).save(orderEntityArgumentCaptor.capture());
        OrderEntity capturedOrderEntity = orderEntityArgumentCaptor.getValue();
        assertEquals(OrderStatus.CONFIRMED, capturedOrderEntity.getStatus());
        verify(orderAdapter, times(1)).adapt(any());
    }

    @Test(expected = OrderNotFoundRuntimeException.class)
    public void updateOrderStatus_WhenOrderDoesNotExists() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);
    }
}
