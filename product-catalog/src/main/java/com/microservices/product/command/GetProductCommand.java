package com.microservices.product.command;

import com.microservices.product.exception.ProductNotFoundRuntimeException;
import com.microservices.product.model.Product;
import com.microservices.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class GetProductCommand implements BaseCommand<Product> {

    private final Long productId;

    @Autowired
    private ProductService productService;

    @Override
    public Product execute() {
        List<Product> products = productService.getProducts(Set.of(productId));
        if (products.isEmpty()) {
            throw new ProductNotFoundRuntimeException();
        }
        return products.get(0);
    }
}
