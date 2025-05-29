package com.microservices.product.adapter;


import com.microservices.product.entity.ProductEntity;
import com.microservices.product.model.Product;
import com.microservices.product.model.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductAdapterTest {

    private final ProductAdapter productAdapter = new ProductAdapter();

    @Test
    public void adaptTest() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(1L);
        productEntity.setStock(100);
        productEntity.setName("Name");
        productEntity.setDescription("Description");
        productEntity.setCategory(ProductCategory.COMPUTERS);

        Product product = productAdapter.adapt(productEntity);

        assertEquals(productEntity.getProductId(), product.getProductId());
        assertEquals(productEntity.getStock(), product.getProductStock());
        assertEquals(productEntity.getName(), product.getProductName());
        assertEquals(productEntity.getDescription(), product.getProductDescription());
        assertEquals(productEntity.getCategory(), product.getProductCategory());
    }
}
