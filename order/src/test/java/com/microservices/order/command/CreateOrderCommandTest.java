package com.microservices.order.command;

import com.microservices.order.exception.OutOfStockRuntimeException;
import com.microservices.order.model.CreateOrder;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateOrderCommandTest {

    private CreateOrder createOrder;

    @Mock
    private OrderService orderService;
    @Mock
    private ProductCatalogService productCatalogService;
    @Mock
    private NotifySearchEngineService notifySearchEngineService;
    @InjectMocks
    private CreateOrderCommand createOrderCommand;

    @Before
    public void setUp() {
        createOrder = new CreateOrder();
        createOrder.setCustomerId(1L);
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductId(5L);
        productQuantity.setQuantity(5);
        createOrder.setProductQuantities(List.of(productQuantity));

        createOrderCommand = new CreateOrderCommand(createOrder);
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = OutOfStockRuntimeException.class)
    public void executeTest_WhenProductsOutOfStock() {
        when(productCatalogService.updateStock(any())).thenReturn(false);
        when(orderService.createOrder(any(), any())).thenReturn(new Order());

        createOrderCommand.execute();

        verify(orderService, times(1)).createOrder(eq(createOrder.getCustomerId()), any());
        verify(orderService, times(1)).updateOrderStatus(any(), eq(OrderStatus.CANCELED));
        verify(notifySearchEngineService, times(2)).sendOrder(any());
    }

    @Test
    public void executeTest_WhenProductsInStock() {
        when(productCatalogService.updateStock(any())).thenReturn(true);
        when(orderService.createOrder(any(), any())).thenReturn(new Order());
        when(orderService.updateOrderStatus(any(), any())).thenReturn(new Order());

        createOrderCommand.execute();

        verify(orderService, times(1)).createOrder(eq(createOrder.getCustomerId()), any());
        verify(orderService, times(1)).updateOrderStatus(any(), eq(OrderStatus.CONFIRMED));
        verify(notifySearchEngineService, times(2)).sendOrder(any());
    }

}
