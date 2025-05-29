package com.microservices.product.adapter;

import com.microservices.product.entity.ProductEntity;
import com.microservices.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductAdapter {

    public Product adapt(ProductEntity productEntity) {
        Product product = new Product();
        product.setProductId(productEntity.getProductId());
        product.setProductStock(productEntity.getStock());
        product.setProductName(productEntity.getName());
        product.setProductDescription(productEntity.getDescription());
        product.setProductCategory(productEntity.getCategory());
        return product;
    }
}
