package com.microservices.search.adapter;

import com.microservices.clients.dto.Product;
import com.microservices.search.document.OrderDocument;
import com.microservices.search.model.OrderResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderDocumentAdapter {

    private final ItemDocumentAdapter itemDocumentAdapter;

    public OrderDocument adapt(OrderDocument orderDocument, OrderResource orderResource, Map<Long, Product> productMap) {
        if (orderDocument == null) {
            orderDocument = new OrderDocument();
            orderDocument.setOrderId(orderResource.getOrderId());
        }
        orderDocument.setOrderStatus(orderResource.getOrderStatus());
        orderDocument.setCreatedAt(orderResource.getCreatedAt().toLocalDate());
        orderDocument.setUpdatedAt(orderResource.getUpdatedAt().toLocalDate());
        if (orderResource.getItems() != null) {
            orderDocument.setItems(orderResource.getItems()
                    .stream()
                    .map(i -> itemDocumentAdapter.adapt(i, productMap))
                    .collect(Collectors.toList()));
        }

        return orderDocument;
    }
}
