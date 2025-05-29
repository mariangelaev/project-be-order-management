package com.microservices.order.controller;

import com.microservices.order.command.*;
import com.microservices.order.model.CreateOrder;
import com.microservices.order.model.Order;
import com.microservices.order.model.ProductQuantity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    private OrderController orderController;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private CreateOrderCommand createOrderCommand;

    @Mock
    private DeleteOrderCommand deleteOrderCommand;

    @Mock
    private GetOrderCommand getOrderCommand;

    @Mock
    private UpdateOrderCommand updateOrderCommand;

    @Before
    public void setUp() {
        orderController = new OrderController(beanFactory);
    }

    @Test
    public void createOrderTest() {
        CreateOrder createOrder = new CreateOrder();
        Order expectedResource = new Order();

        when(beanFactory.getBean(CreateOrderCommand.class, createOrder)).thenReturn(createOrderCommand);
        when(createOrderCommand.execute()).thenReturn(expectedResource);

        ResponseEntity<Order> response = orderController.createOrder(createOrder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResource, response.getBody());
    }

    @Test
    public void deleteOrderTest() {
        Long orderId = 1L;
        Order expectedResource = new Order();

        when(beanFactory.getBean(DeleteOrderCommand.class, 1L)).thenReturn(deleteOrderCommand);
        when(deleteOrderCommand.execute()).thenReturn(expectedResource);

        ResponseEntity<Order> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResource, response.getBody());
    }

    @Test
    public void getOrderTest() {
        Long orderId = 1L;
        Order expectedResource = new Order();

        when(beanFactory.getBean(GetOrderCommand.class, 1L)).thenReturn(getOrderCommand);
        when(getOrderCommand.execute()).thenReturn(expectedResource);

        ResponseEntity<Order> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResource, response.getBody());
    }

    @Test
    public void updateOrderTest() {
        List<ProductQuantity> updateOrderDto = Collections.singletonList(new ProductQuantity());
        Order expectedResource = new Order();

        when(beanFactory.getBean(UpdateOrderCommand.class, 1L, updateOrderDto)).thenReturn(updateOrderCommand);
        when(updateOrderCommand.execute()).thenReturn(expectedResource);

        ResponseEntity<Order> response = orderController.updateOrder(1L, updateOrderDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResource, response.getBody());
    }
}
