package com.microservices.search.service;

import com.microservices.clients.ProductCatalogClient;
import com.microservices.clients.dto.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductCatalogService {

    private final ProductCatalogClient productCatalogClient;

    //@Cacheable(value = "products") fixme: should be cached
    public Optional<Product> getProduct(Long productId) {
        ResponseEntity<Product> product = productCatalogClient.getProduct(productId.toString());
        log.info("ProductId {} - Response body {}", productId, product);
        if (product.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(product.getBody());
        }
        return Optional.empty();
    }
}
