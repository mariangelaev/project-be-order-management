package com.microservices.order.command;

import com.microservices.clients.dto.UpdateProductStock;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.entity.OrderEntity;
import com.microservices.order.exception.ProductCatalogRuntimeException;
import com.microservices.order.exception.StatusOrderRuntimeException;
import com.microservices.order.model.Item;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteOrderCommandTest {

    @Mock
    private OrderService orderService;
    @Mock
    private ProductCatalogService productCatalogService;
    @Mock
    private NotifySearchEngineService notifySearchEngineService;
    @Captor
    private ArgumentCaptor<List<UpdateProductStock>> argumentCaptor;
    @InjectMocks
    private DeleteOrderCommand deleteOrderCommand;

    @Before
    public void setUp() {
        deleteOrderCommand = new DeleteOrderCommand(2L);
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = StatusOrderRuntimeException.class)
    public void executeTest_WhenStatusOrderIsNotCompleted() {
        Order order = getOrder(OrderStatus.CANCELED);
        Mockito.when(orderService.getOrder(any())).thenReturn(order);

        deleteOrderCommand.execute();
    }

    @Test(expected = ProductCatalogRuntimeException.class)
    public void executeTest_WhenUpdateStockFails() {
        Order order = getOrder(OrderStatus.CONFIRMED);
        Mockito.when(orderService.getOrder(any())).thenReturn(order);
        Mockito.when(productCatalogService.updateStock(anyList())).thenReturn(false);

        deleteOrderCommand.execute();
    }

    @Test
    public void executeTest_WhenUpdateStockSucceeded() {
        Order order = getOrder(OrderStatus.CONFIRMED);
        Mockito.when(orderService.getOrder(any())).thenReturn(order);
        Mockito.when(productCatalogService.updateStock(anyList())).thenReturn(true);
        Mockito.when(orderService.updateOrderStatus(any(), any())).thenReturn(order);

        Order actualOrder = deleteOrderCommand.execute();

        verify(productCatalogService, times(1)).updateStock(argumentCaptor.capture());
        List<UpdateProductStock> updateProductStocks = argumentCaptor.getValue();
        assertEquals(actualOrder.getItems().size(), updateProductStocks.size());
        assertEquals(actualOrder.getItems().get(0).getQuantity(), updateProductStocks.get(0).getQuantity());
        assertEquals(actualOrder.getItems().get(0).getProductId(), updateProductStocks.get(0).getProductId());
        verify(orderService, times(1)).updateOrderStatus(any(), eq(OrderStatus.CANCELED));
        verify(notifySearchEngineService, times(1)).sendOrder(any());
    }


    private Order getOrder(OrderStatus status) {
        Order order = new Order();
        order.setOrderId(2L);
        order.setOrderStatus(status);
        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(5);
        order.setItems(List.of(item));
        return order;
    }
}
