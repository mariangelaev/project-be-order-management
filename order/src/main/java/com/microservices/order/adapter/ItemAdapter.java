package com.microservices.order.adapter;

import com.microservices.order.entity.ItemEntity;
import com.microservices.order.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemAdapter {

    public Item adapt(ItemEntity entity) {
        Item item = new Item();
        item.setItemId(entity.getItemId());
        item.setQuantity(entity.getQuantity());
        item.setProductId(entity.getProductId());
        item.setCreatedAt(entity.getCreatedUpdatedAt().getCreatedAt());
        item.setUpdatedAt(entity.getCreatedUpdatedAt().getUpdatedAt());
        return item;
    }
}
