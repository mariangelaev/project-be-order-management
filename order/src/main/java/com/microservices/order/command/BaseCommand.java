package com.microservices.order.command;

public abstract class BaseCommand<R> {

    public abstract R execute();
}
