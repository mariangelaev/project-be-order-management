package com.microservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OutOfStockRuntimeException extends RuntimeException {

    public OutOfStockRuntimeException() {
        super("One or more products out of stock");
    }
}
