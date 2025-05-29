package com.microservices.product.command;

import com.microservices.product.model.Product;
import com.microservices.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class GetAllProductsCommand implements BaseCommand<List<Product>> {

    @Autowired
    private ProductService productService;

    @Override
    public List<Product> execute() {
        return productService.getProducts();
    }
}
