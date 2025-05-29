package com.microservices.product.service;

import com.microservices.product.adapter.ProductAdapter;
import com.microservices.product.entity.ProductEntity;
import com.microservices.product.exception.ProductOutOfStockRuntimeException;
import com.microservices.product.model.Product;
import com.microservices.product.model.UpdateProductStock;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductAdapter adapter;

    public List<Product> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(adapter::adapt)
                .collect(Collectors.toList());
    }

    public List<Product> getProducts(Set<Long> productIds) {
        return productRepository.findAllById(productIds)
                .stream()
                .map(adapter::adapt)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Product> updateStocks(List<UpdateProductStock> updateProductStocks) {

        Map<Long, Integer> deltaQuantities = updateProductStocks.stream()
                .collect(Collectors.toMap(UpdateProductStock::getProductId, UpdateProductStock::getQuantity));

        log.info("Thread {} trying to fetch products ID {}", Thread.currentThread().getName(), deltaQuantities.keySet());
        List<ProductEntity> productEntityList = productRepository.findProductsWithLock(deltaQuantities.keySet());

        log.info("Thread {} acquired the lock on products ID {}", Thread.currentThread().getName(), deltaQuantities.keySet());
        for (ProductEntity e : productEntityList) {
            if (!canUpdateStock(e, deltaQuantities.get(e.getProductId()))) {
                throw new ProductOutOfStockRuntimeException(e.getProductId());
            }
            e.setStock(e.getStock() + deltaQuantities.get(e.getProductId()));
            log.info("Product ID {} stock updated to {}", e.getProductId(), e.getStock());
        };

        return productRepository.saveAll(productEntityList)
                .stream()
                .map(adapter::adapt)
                .collect(Collectors.toList());
    }


    private boolean canUpdateStock(ProductEntity product, int quantity) {
        Integer currentStock = product.getStock();
        int quantityAbs = Math.abs(quantity);

        if (quantity < 0 && currentStock < quantityAbs) {
            log.info("Product ID {} out of stock {} for delta {}", product.getProductId(), currentStock, quantity);
            return false;
        }
        log.info("Product ID {} in stock {} for delta {}", product.getProductId(), currentStock, quantity);
        return true;
    }
}
