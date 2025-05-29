package com.microservices.search.adapter;

import com.microservices.search.document.OrderDocument;
import com.microservices.search.model.ItemResource;
import com.microservices.search.model.OrderResource;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
public class OrderDocumentAdapterTest {

    private final ItemDocumentAdapter itemDocumentAdapter = mock(ItemDocumentAdapter.class);
    private OrderDocumentAdapter orderDocumentAdapter;

    @Before
    public void setUp() {
        orderDocumentAdapter = new OrderDocumentAdapter(itemDocumentAdapter);
    }

    @Test
    public void adaptTest() {
        OrderResource order = new OrderResource();
        order.setOrderId(1L);
        order.setOrderStatus("Status");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        ItemResource item = new ItemResource();
        order.setItems(List.of(item));

        OrderDocument orderDocument = orderDocumentAdapter.adapt(null, order, new HashMap<>());

        assertEquals(order.getOrderId(), orderDocument.getOrderId());
        assertEquals(order.getOrderStatus(), orderDocument.getOrderStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        verify(itemDocumentAdapter, times(1)).adapt(any(), anyMap());
    }
}
