package com.microservices.search.command;

public interface BaseCommand<R> {

    R execute();
}
