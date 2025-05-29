package com.microservices.order.adapter;

import com.microservices.order.entity.CreatedUpdatedAtEntity;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.model.Item;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


public class ItemAdapterTest {

    private final ItemAdapter itemAdapter = new ItemAdapter();

    @Test
    public void toModelTest() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1L);
        itemEntity.setQuantity(5);
        itemEntity.setProductId(3L);
        CreatedUpdatedAtEntity createdUpdatedAt = new CreatedUpdatedAtEntity();
        createdUpdatedAt.setCreatedAt(LocalDateTime.now());
        createdUpdatedAt.setUpdatedAt(LocalDateTime.now());
        itemEntity.setCreatedUpdatedAt(createdUpdatedAt);

        Item resource = itemAdapter.adapt(itemEntity);
        assertEquals(itemEntity.getItemId(), resource.getItemId());
        assertEquals(itemEntity.getQuantity(), resource.getQuantity());
        assertEquals(itemEntity.getProductId(), resource.getProductId());
        assertEquals(itemEntity.getCreatedUpdatedAt().getCreatedAt(), resource.getCreatedAt());
        assertEquals(itemEntity.getCreatedUpdatedAt().getUpdatedAt(), resource.getUpdatedAt());
    }
}
