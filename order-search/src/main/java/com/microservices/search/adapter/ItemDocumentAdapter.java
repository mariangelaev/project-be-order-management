package com.microservices.search.adapter;

import com.microservices.clients.dto.Product;
import com.microservices.search.document.ItemDocument;
import com.microservices.search.model.ItemResource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ItemDocumentAdapter {

    public ItemDocument adapt(ItemResource itemResource, Map<Long, Product> productMap) {
        ItemDocument itemDocument = new ItemDocument();
        itemDocument.setItemId(itemResource.getItemId());
        itemDocument.setQuantity(itemResource.getQuantity());
        itemDocument.setProductId(itemResource.getProductId().toString());
        itemDocument.setProductName(productMap.get(itemResource.getProductId()).getProductName());
        itemDocument.setProductDescription(productMap.get(itemResource.getProductId()).getProductDescription());
        return itemDocument;
    }
}
