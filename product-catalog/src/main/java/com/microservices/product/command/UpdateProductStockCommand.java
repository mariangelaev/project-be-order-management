package com.microservices.product.command;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.exception.ProductNotFoundRuntimeException;
import com.microservices.product.exception.ProductOutOfStockRuntimeException;
import com.microservices.product.model.Product;
import com.microservices.product.model.UpdateProductStock;
import com.microservices.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateProductStockCommand implements BaseCommand<List<Product>> {

    private final List<UpdateProductStock> request;

    @Autowired
    private ProductService productService;

    @Override
    public List<Product> execute() {

        if (!doProductsExist()) {
            throw new ProductNotFoundRuntimeException();
        }
        return productService.updateStocks(request);
    }

    private boolean doProductsExist() {
        Set<Long> productIds = request.stream()
                .map(UpdateProductStock::getProductId)
                .collect(Collectors.toSet());
        List<Product> products = productService.getProducts(productIds);
        return products.size() == productIds.size();
    }
}
