package com.microservices.product.command;

import com.microservices.product.entity.ProductEntity;
import com.microservices.product.model.Product;
import com.microservices.product.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAllProductsCommandTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private GetAllProductsCommand command;

    @Before
    public void setUp() {
        command = new GetAllProductsCommand();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void executeTest_WhenProductsDoNotExist() {
        when(productService.getProducts()).thenReturn(Collections.emptyList());
        assertTrue(command.execute().isEmpty());
    }

    @Test
    public void executeTest_WhenProductsExist() {
        when(productService.getProducts()).thenReturn(getEntities());
        assertEquals(getEntities(), command.execute());
    }

    public List<Product> getEntities() {
        return List.of(new Product(), new Product());
    }
}
