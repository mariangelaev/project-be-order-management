package com.microservices.search.adapter;

import com.microservices.clients.dto.Product;
import com.microservices.search.document.ItemDocument;
import com.microservices.search.model.ItemResource;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ItemDocumentAdapterTest {

    private ItemDocumentAdapter itemDocumentAdapter = new ItemDocumentAdapter();

    @Test
    public void adaptTest() {
        ItemResource itemResource = new ItemResource();
        itemResource.setItemId(1L);
        itemResource.setQuantity(10);
        itemResource.setProductId(100L);

        Product product = new Product();
        product.setProductName("Name");
        product.setProductDescription("Description");

        Map<Long, Product> productMap = new HashMap<>();
        productMap.put(100L, product);

        ItemDocument itemDocument = itemDocumentAdapter.adapt(itemResource, productMap);

        assertEquals(itemDocument.getItemId(), itemResource.getItemId());
        assertEquals(itemDocument.getProductId(), itemResource.getProductId().toString());
        assertEquals(itemDocument.getQuantity().intValue(), itemResource.getQuantity());
        assertEquals(itemDocument.getProductName(), product.getProductName());
        assertEquals(itemDocument.getProductDescription(), product.getProductDescription());
    }
}
