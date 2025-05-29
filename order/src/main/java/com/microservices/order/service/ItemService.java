package com.microservices.order.service;

import com.microservices.order.adapter.ItemAdapter;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.model.Item;
import com.microservices.order.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemAdapter adapter;

    public Item updateQuantity(Long itemId, Integer newQuantity) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow();
        itemEntity.setQuantity(newQuantity);
        return adapter.adapt(itemRepository.save(itemEntity));
    }
}
