package com.microservices.search.service;

import com.microservices.clients.ProductCatalogClient;
import com.microservices.clients.dto.Product;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductCatalogServiceTest {

    private final ProductCatalogClient productCatalogClient = mock(ProductCatalogClient.class);
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
        productCatalogService = new ProductCatalogService(productCatalogClient);
    }

    @Test
    public void getProduct_WhenProductFound() {
        ResponseEntity<Product> productResponseEntity = new ResponseEntity<>(new Product(), HttpStatusCode.valueOf(200));
        when(productCatalogClient.getProduct(any())).thenReturn(productResponseEntity);

        Optional<Product> product = productCatalogService.getProduct(1L);

        assertTrue(product.isPresent());
    }

    @Test
    public void getProduct_WhenProductNotFound() {
        ResponseEntity<Product> productResponseEntity = new ResponseEntity<>(HttpStatusCode.valueOf(404));
        when(productCatalogClient.getProduct(any())).thenReturn(productResponseEntity);

        Optional<Product> product = productCatalogService.getProduct(1L);

        assertFalse(product.isPresent());
    }
}
