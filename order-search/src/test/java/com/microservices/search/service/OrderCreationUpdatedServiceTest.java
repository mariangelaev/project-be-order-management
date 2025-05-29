package com.microservices.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.search.adapter.OrderDocumentAdapter;
import com.microservices.search.model.ItemResource;
import com.microservices.search.model.OrderResource;
import com.microservices.search.repository.OrderRepository;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderCreationUpdatedServiceTest {

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderDocumentAdapter orderDocumentAdapter = mock(OrderDocumentAdapter.class);
    private final ProductCatalogService productCatalogService = mock(ProductCatalogService.class);
    private OrderCreationUpdateService orderCreationUpdateService;

    @Before
    public void setUp() {
        orderCreationUpdateService = new OrderCreationUpdateService(objectMapper,
                orderRepository,
                orderDocumentAdapter,
                productCatalogService);
    }

    @Test
    @SneakyThrows
    public void getMessageTest() {
        OrderResource orderResource = new OrderResource();
        orderResource.setItems(List.of(new ItemResource(), new ItemResource()));
        when(objectMapper.readValue(anyString(), eq(OrderResource.class))).thenReturn(orderResource);
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        orderCreationUpdateService.getMessage("message");

        verify(productCatalogService, times(2)).getProduct(any());
        verify(orderDocumentAdapter, times(1)).adapt(any(), any(), anyMap());
        verify(orderRepository, times(1)).save(any());
    }
}
