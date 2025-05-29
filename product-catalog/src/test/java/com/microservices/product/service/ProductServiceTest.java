package com.microservices.product.service;

import com.microservices.product.adapter.ProductAdapter;
import com.microservices.product.entity.ProductEntity;
import com.microservices.product.exception.ProductOutOfStockRuntimeException;
import com.microservices.product.model.Product;
import com.microservices.product.model.UpdateProductStock;
import com.microservices.product.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ProductServiceTest {

    private ProductService productService;
    private final ProductAdapter adapter = new ProductAdapter();
    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);

    @Before
    public void setUp() {
        this.productService = new ProductService(productRepository, adapter);
    }

    @Test
    public void getProductsTest_WhenNoProductExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(productService.getProducts().isEmpty());
    }

    @Test
    public void getProductsTest_WhenProductsExist() {
        List<ProductEntity> entities = createEntities();
        when(productRepository.findAll()).thenReturn(entities);

        List<Product> products = productService.getProducts();

        assertFalse(products.isEmpty());
        assertEquals(entities.size(), products.size());
    }

    @Test
    public void getProductsTest_WhenProductIdsAreGiven() {
        List<ProductEntity> entities = createEntities();
        when(productRepository.findAllById(any())).thenReturn(entities);

        List<Product> products = productService.getProducts(Set.of(1L, 2L));

        assertFalse(products.isEmpty());
        assertEquals(entities.size(), products.size());
    }

    @Test(expected = ProductOutOfStockRuntimeException.class)
    public void updateStocks_WhenProductOutOfStock() {
        List<UpdateProductStock> updateProductStocks = getUpdateProducts();
        updateProductStocks.get(0).setQuantity(-100);
        when(productRepository.findProductsWithLock(any())).thenReturn(createEntities());

        productService.updateStocks(updateProductStocks);
    }

    @Test
    public void updateStocks_WhenProductInStock() {
        List<UpdateProductStock> updateProductStocks = getUpdateProducts();
        List<ProductEntity> entities = createEntities();
        when(productRepository.findProductsWithLock(any())).thenReturn(entities);
        when(productRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Product> products = productService.updateStocks(updateProductStocks);

        assertFalse(products.isEmpty());
        assertEquals(entities.size(), products.size());
        assertEquals(entities.get(0).getStock().intValue(), products.get(0).getProductStock().intValue());
        assertEquals(entities.get(1).getStock().intValue(), products.get(1).getProductStock().intValue());
    }

    private List<ProductEntity> createEntities() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(20);
        ProductEntity product2 = new ProductEntity();
        product2.setProductId(2L);
        product2.setStock(5);
        return List.of(product1, product2);
    }

    private List<UpdateProductStock> getUpdateProducts() {
        UpdateProductStock updateProduct1Stock = new UpdateProductStock();
        updateProduct1Stock.setProductId(1L);
        updateProduct1Stock.setQuantity(-1);
        UpdateProductStock updateProduct2Stock = new UpdateProductStock();
        updateProduct2Stock.setProductId(2L);
        updateProduct2Stock.setQuantity(-5);
        return List.of(updateProduct1Stock, updateProduct2Stock);
    }
}
