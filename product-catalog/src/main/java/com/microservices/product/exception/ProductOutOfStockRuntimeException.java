package com.microservices.product.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@Setter
@ResponseStatus(HttpStatus.CONFLICT)
public class ProductOutOfStockRuntimeException extends RuntimeException {

    public ProductOutOfStockRuntimeException(Long productId) {
        super("Out of stock for Product ID "+ productId);
    }
}
