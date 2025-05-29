package com.microservices.order.service;

import com.microservices.clients.ProductCatalogClient;
import com.microservices.clients.dto.Product;
import com.microservices.clients.dto.UpdateProductStock;
import feign.FeignException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductCatalogServiceTest {

    private final ProductCatalogClient productCatalogClient = Mockito.mock(ProductCatalogClient.class);
    private final ArgumentCaptor<List<UpdateProductStock>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
        this.productCatalogService = new ProductCatalogService(productCatalogClient);
    }

    @Test
    public void updateStockTest_When200Code() {
        when(productCatalogClient.updateProductsStock(any())).thenReturn(ResponseEntity.ok(List.of(new Product())));
        UpdateProductStock updateProductStock = new UpdateProductStock(2L, 5);

        Assert.assertTrue(productCatalogService.updateStock(List.of(updateProductStock)));
        verify(productCatalogClient, Mockito.times(1)).updateProductsStock(argumentCaptor.capture());
        List<UpdateProductStock> capturedStocks = argumentCaptor.getValue();
        Assert.assertEquals(1, capturedStocks.size());
        Assert.assertEquals(Long.valueOf(2), capturedStocks.get(0).getProductId());
        Assert.assertEquals(5, capturedStocks.get(0).getQuantity());
    }

    @Test
    public void updateStockTest_When400Code() {
        ResponseEntity<List<Product>> responseEntity = ResponseEntity.badRequest().build();
        when(productCatalogClient.updateProductsStock(any())).thenReturn(responseEntity);
        UpdateProductStock updateProductStock = new UpdateProductStock(2L, 5);

        assertFalse(productCatalogService.updateStock(List.of(updateProductStock)));
    }

    @Test
    public void updateStockTest_When500Code() {
        ResponseEntity<List<Product>> responseEntity = ResponseEntity.internalServerError().build();
        when(productCatalogClient.updateProductsStock(any())).thenReturn(responseEntity);
        UpdateProductStock updateProductStock = new UpdateProductStock(2L, 5);

        assertFalse(productCatalogService.updateStock(List.of(updateProductStock)));
    }

    @Test
    public void updateStockTest_WhenFeignClientException() {
        ResponseEntity<List<Product>> responseEntity = ResponseEntity.internalServerError().build();
        when(productCatalogClient.updateProductsStock(any())).thenThrow(FeignException.FeignClientException.class);
        UpdateProductStock updateProductStock = new UpdateProductStock(2L, 5);

        assertFalse(productCatalogService.updateStock(List.of(updateProductStock)));
    }
}
