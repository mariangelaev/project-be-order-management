package com.microservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class StatusOrderRuntimeException extends RuntimeException{

    public StatusOrderRuntimeException() {
        super("The order can't be canceled in the current state");
    }
}
