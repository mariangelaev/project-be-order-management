package com.microservices.order.service;

import com.microservices.order.adapter.ItemAdapter;
import com.microservices.order.entity.ItemEntity;
import com.microservices.order.repository.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemServiceTest {

    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final ItemAdapter itemAdapter = Mockito.mock(ItemAdapter.class);
    private final ArgumentCaptor<ItemEntity> itemEntityArgumentCaptor = ArgumentCaptor.forClass(ItemEntity.class);

    private ItemService itemService;

    @Before
    public void setUp() {
        this.itemService = new ItemService(itemRepository,
                itemAdapter);
    }

    @Test
    public void updateQuantityTest_WhenItemExists() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(new ItemEntity()));

        itemService.updateQuantity(2L, 5);

        verify(itemRepository, Mockito.times(1)).save(itemEntityArgumentCaptor.capture());
        assertEquals(5, itemEntityArgumentCaptor.getValue().getQuantity());
    }

    @Test(expected = NoSuchElementException.class)
    public void updateQuantityTest_WhenItemDoesNotExists() {
        when(itemRepository.findById(any())).thenThrow(NoSuchElementException.class);

        itemService.updateQuantity(2L, 5);
    }
}
