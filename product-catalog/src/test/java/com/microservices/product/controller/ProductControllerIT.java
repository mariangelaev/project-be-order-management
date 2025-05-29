package com.microservices.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.entity.ProductEntity;
import com.microservices.product.model.ProductCategory;
import com.microservices.product.model.UpdateProductStock;
import com.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MariaDBContainer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private ProductEntity product;
    static MariaDBContainer mariaDBContainer = new MariaDBContainer("mariadb:11.4");

    @DynamicPropertySource
    static void setUpProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mariaDBContainer.start();
    }

    @BeforeEach
    public void beforeEach() {
        product = new ProductEntity();
        product.setStock(5);
        product.setCategory(ProductCategory.BOOKS);
        product.setName("Name");
        product.setDescription("Description");
        product = productRepository.save(product);
    }

    @AfterEach
    public void afterEach() {
        productRepository.deleteAll();
    }

    @Test
    public void getProductTest_WhenProductExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/products/{productId}", product.getProductId().toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productDescription").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productStock").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCategory").exists());
    }

    @Test
    public void getProductTest_WhenProductDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/products/{productId}", 500))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProductsStockTest_WhenSingleProductDoesNotExists() throws Exception {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(500L);
        updateProductStock.setQuantity(5);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(List.of(updateProductStock))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProductsStockTest_WhenSingleProductExists_AndInStock() throws Exception {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(product.getProductId());
        updateProductStock.setQuantity(-product.getStock());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(List.of(updateProductStock))))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId").value(product.getProductId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productName").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productDescription").value(product.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productStock").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productCategory").value(product.getCategory().toString()));
    }

    @Test
    public void updateProductsStockTest_WhenSingleProductExists_AndNotInStock() throws Exception {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(product.getProductId());
        updateProductStock.setQuantity(-10);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(List.of(updateProductStock))))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateProductsStockTest_WhenSingleProductExists_AndInStock_AndConcurrentStockUpdate() throws Exception {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(product.getProductId());
        updateProductStock.setQuantity(-product.getStock());

        AtomicReference<ResultActions> firstResult = new AtomicReference<>();
        Thread firstThread = new Thread(() -> {
            try {
                firstResult.set(mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(List.of(updateProductStock)))));
            } catch (Exception e) {
                fail();
            }
        });

        AtomicReference<ResultActions> secondResult = new AtomicReference<>();
        Thread secondThread = new Thread(() -> {
            try {
                secondResult.set(mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/products")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(List.of(updateProductStock)))));
            } catch (Exception e) {
                fail();
            }
        });

        firstThread.start();
        secondThread.start();
        firstThread.join();
        secondThread.join();

        firstResult.get().andExpect(result -> {
            assertThat(result.getResponse().getStatus(), anyOf(is(200), is(409)));
        });
        secondResult.get().andExpect(result -> {
            assertThat(result.getResponse().getStatus(), anyOf(is(200), is(409)));
        });
    }
}
