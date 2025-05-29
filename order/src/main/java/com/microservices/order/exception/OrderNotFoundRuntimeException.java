package com.microservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderNotFoundRuntimeException extends RuntimeException {

    public OrderNotFoundRuntimeException(Long orderId) {
        super("Order with ID "+ orderId +" not found");
    }
}
