package com.microservices.search.controller;

import com.microservices.search.command.SearchOrdersCommand;
import com.microservices.search.document.OrderDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/orders", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class SearchOrderController {

    private final BeanFactory beanFactory;

    @GetMapping
    public ResponseEntity<Page<OrderDocument>> searchOrders(@RequestParam(required = false, value = "productName") String productName,
                                                            @RequestParam(required = false, value = "productDescription") String productDescription,
                                                            @RequestParam(required = false, value = "statusOrder") String status,
                                                            @RequestParam(required = false, value = "date") LocalDate creationDate,
                                                            @RequestParam(value = "page") String page,
                                                            @RequestParam(value = "size") String size) {
        log.info("Received call to GET api/v1/orders with params - " +
                "Product: {} - ProductDescription: {} - StatusOrder: {} " +
                "- Date: {} - Page: {} - Size: {}", productName, productDescription, status, creationDate, page, size);
        Page<OrderDocument> orderDocuments = beanFactory.getBean(SearchOrdersCommand.class, productName,
                productDescription,
                status,
                creationDate,
                page,
                size).execute();
        log.info("Retrieved orders {}", orderDocuments);
        return new ResponseEntity<>(orderDocuments, HttpStatus.OK);
    }
}
