package com.microservices.product.controller;

import com.microservices.product.command.GetAllProductsCommand;
import com.microservices.product.command.GetProductCommand;
import com.microservices.product.command.UpdateProductStockCommand;
import com.microservices.product.model.Product;
import com.microservices.product.model.UpdateProductStock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/products", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ProductController {

    private final BeanFactory beanFactory;

    @PatchMapping()
    public ResponseEntity<List<Product>> updateProductsStock(@RequestBody List<UpdateProductStock> request) {
        log.info("Received call to PATCH api/v1/products with request {}", request);
        UpdateProductStockCommand updateProductStockCommand = beanFactory.getBean(UpdateProductStockCommand.class, request);
        List<Product> products = updateProductStockCommand.execute();
        log.info("Updated products {}", products);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable(name = "productId") String productId) {
        log.info("Received call to GET api/v1/products/{}", productId);
        GetProductCommand getProductCommand = beanFactory.getBean(GetProductCommand.class, Long.parseLong(productId));
        Product product = getProductCommand.execute();
        log.info("Retrieved product {}", product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts() {
        GetAllProductsCommand getProductsCommand = beanFactory.getBean(GetAllProductsCommand.class);
        log.info("Received call to GET api/v1/products");
        List<Product> products = getProductsCommand.execute();
        log.info("Retrieved products {}", products);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
