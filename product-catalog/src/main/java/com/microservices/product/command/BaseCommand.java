package com.microservices.product.command;

public interface BaseCommand<R> {

    R execute();
}
