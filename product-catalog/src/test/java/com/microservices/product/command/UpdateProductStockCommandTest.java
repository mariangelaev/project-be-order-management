package com.microservices.product.command;

import com.microservices.product.exception.ProductNotFoundRuntimeException;
import com.microservices.product.model.Product;
import com.microservices.product.model.UpdateProductStock;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateProductStockCommandTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private UpdateProductStockCommand command;

    @Before
    public void setUp() {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(1L);
        updateProductStock.setQuantity(-5);

        command = new UpdateProductStockCommand(List.of(updateProductStock));
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = ProductNotFoundRuntimeException.class)
    public void executeTest_WhenProductDoesNotExist() {
        when(productService.getProducts(any())).thenReturn(Collections.emptyList());

        command.execute();
    }

    @Test
    public void executeTest_WhenProductsExist() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductStock(5);
        when(productService.getProducts(any())).thenReturn(List.of(product));

        command.execute();

        verify(productService, times(1)).updateStocks(any());
    }

}
