package com.microservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProductCatalogRuntimeException extends RuntimeException {

    public ProductCatalogRuntimeException() {
        super("Error while updating product stock");
    }
}
