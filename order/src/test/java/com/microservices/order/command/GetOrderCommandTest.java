package com.microservices.order.command;

import com.microservices.order.model.Order;
import com.microservices.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GetOrderCommandTest {

    @InjectMocks
    private GetOrderCommand getOrderCommand;
    @Mock
    private OrderService orderService;

    @Before
    public void setUp() {
        getOrderCommand = new GetOrderCommand(2L);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void executeTest() {
        Mockito.when(orderService.getOrder(any())).thenReturn(new Order());

        Order actualOrder = getOrderCommand.execute();

        verify(orderService, times(1)).getOrder(any());
        assertNotNull(actualOrder);
    }
}
