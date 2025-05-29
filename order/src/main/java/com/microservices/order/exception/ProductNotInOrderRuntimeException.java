package com.microservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProductNotInOrderRuntimeException extends RuntimeException{

    public ProductNotInOrderRuntimeException(Long productId) {
        super("Product ID "+productId+" not present in the order");
    }
}
