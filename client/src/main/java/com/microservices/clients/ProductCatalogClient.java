package com.microservices.clients;

import com.microservices.clients.dto.Product;
import com.microservices.clients.dto.UpdateProductStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "product-catalog", url = "http://localhost:8081")
public interface ProductCatalogClient {

    @RequestMapping(method = RequestMethod.PATCH, path = "/api/v1/products")
    ResponseEntity<List<Product>> updateProductsStock(@RequestBody List<UpdateProductStock> request);

    @RequestMapping(method = RequestMethod.GET, path = "/api/v1/products/{productId}")
    ResponseEntity<Product> getProduct(@PathVariable(name = "productId") String productId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/v1/products")
    ResponseEntity<List<Product>> getAllProducts();
}
