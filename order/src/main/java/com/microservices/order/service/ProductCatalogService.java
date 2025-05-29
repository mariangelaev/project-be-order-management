package com.microservices.order.service;

import com.microservices.clients.ProductCatalogClient;
import com.microservices.clients.dto.Product;
import com.microservices.clients.dto.UpdateProductStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final ProductCatalogClient productCatalogClient;

    public boolean updateStock(List<UpdateProductStock> updateProductStocks) {
        ResponseEntity<List<Product>> response = null;
        try {
             response = productCatalogClient.updateProductsStock(updateProductStocks);
        } catch (Exception e) {
            log.info("Catched exception while calling PATCH /api/v1/products: {}", e.getCause());
            return false;
        }
        log.info("Request body {} - Response body {}", updateProductStocks, response);
        return response != null && response.getStatusCode().is2xxSuccessful();
    }
}
