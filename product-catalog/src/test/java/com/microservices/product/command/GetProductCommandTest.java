package com.microservices.product.command;


import com.microservices.product.exception.ProductNotFoundRuntimeException;
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
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetProductCommandTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private GetProductCommand command;

    @Before
    public void setUp() {
        command = new GetProductCommand(2L);
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = ProductNotFoundRuntimeException.class)
    public void executeTest_WhenProductDoesNotExist() {
        when(productService.getProducts(anySet())).thenReturn(Collections.emptyList());
        command.execute();
    }

    @Test
    public void executeTest_WhenProductExist() {
        Product product = new Product();
        when(productService.getProducts(anySet())).thenReturn(List.of(product));
        assertEquals(product, command.execute());
    }
}
