package com.microservices.order.command;

import com.microservices.clients.dto.UpdateProductStock;
import com.microservices.order.exception.OutOfStockRuntimeException;
import com.microservices.order.exception.ProductNotInOrderRuntimeException;
import com.microservices.order.exception.StatusOrderRuntimeException;
import com.microservices.order.model.*;
import com.microservices.order.service.ItemService;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateOrderCommandTest {

    List<ProductQuantity> request;

    @Mock
    private OrderService orderService;
    @Mock
    private ProductCatalogService productCatalogService;
    @Mock
    private ItemService itemService;
    @Mock
    private NotifySearchEngineService notifySearchEngineService;
    @Captor
    private ArgumentCaptor<List<UpdateProductStock>> updateProductStockArgumentCaptor;
    @InjectMocks
    private UpdateOrderCommand updateOrderCommand;

    @Before
    public void setUp() {
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductId(1L);
        productQuantity.setQuantity(7);

        request = List.of(productQuantity);

        updateOrderCommand = new UpdateOrderCommand(1L, List.of(productQuantity));
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = StatusOrderRuntimeException.class)
    public void executeTest_WhenStatusOrderIsCanceled() {
        Order order = createOrder();
        order.setOrderStatus(OrderStatus.CANCELED);
        Mockito.when(orderService.getOrder(any())).thenReturn(order);

        updateOrderCommand.execute();
    }

    @Test(expected = ProductNotInOrderRuntimeException.class)
    public void executeTest_WhenProductIsNotInTheOrder() {
        Order actualOrder = createOrder();
        actualOrder.getItems().get(0).setProductId(2L);
        when(orderService.getOrder(any())).thenReturn(actualOrder);

        updateOrderCommand.execute();
    }

    @Test(expected = OutOfStockRuntimeException.class)
    public void executeTest_WhenProductOutOfStock() {
        Order actualOrder = createOrder();
        when(orderService.getOrder(any())).thenReturn(actualOrder);
        when(productCatalogService.updateStock(anyList())).thenReturn(false);

        updateOrderCommand.execute();

        verify(productCatalogService, times(1)).updateStock(updateProductStockArgumentCaptor.capture());
        List<UpdateProductStock> capturedUpdateProduct = updateProductStockArgumentCaptor.getValue();
        assertEquals(2, capturedUpdateProduct.get(0).getQuantity());
        verify(notifySearchEngineService, times(0)).sendOrder(any());
    }

    @Test
    public void executeTest_WhenProductInStock() {
        Order actualOrder = createOrder();
        actualOrder.getItems().get(0).setQuantity(10);
        when(orderService.getOrder(any())).thenReturn(actualOrder);
        when(productCatalogService.updateStock(anyList())).thenReturn(true);

        updateOrderCommand.execute();

        verify(productCatalogService, times(1)).updateStock(updateProductStockArgumentCaptor.capture());
        List<UpdateProductStock> capturedUpdateProduct = updateProductStockArgumentCaptor.getValue();
        assertEquals(-3, capturedUpdateProduct.get(0).getQuantity());
        verify(notifySearchEngineService, times(1)).sendOrder(any());
    }

    public Order createOrder() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomerId(1L);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());

        Item item = new Item();
        item.setItemId(1L);
        item.setProductId(1L);
        item.setQuantity(5);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        order.setItems(List.of(item));
        return order;
    }
}
