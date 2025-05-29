package com.microservices.order.controller;

import com.microservices.order.command.CreateOrderCommand;
import com.microservices.order.command.DeleteOrderCommand;
import com.microservices.order.command.GetOrderCommand;
import com.microservices.order.command.UpdateOrderCommand;
import com.microservices.order.model.CreateOrder;
import com.microservices.order.model.Order;
import com.microservices.order.model.ProductQuantity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "api/v1/orders", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class OrderController {

    private final BeanFactory beanFactory;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrder createOrder) {
        log.info("Received call to POST api/v1/orders with request {}", createOrder);
        Order order = beanFactory.getBean(CreateOrderCommand.class, createOrder).execute();
        log.info("Created order {}", order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable(value = "orderId") Long orderId) {
        log.info("Received call to DELETE api/v1/orders/{}", orderId);
        Order order = beanFactory.getBean(DeleteOrderCommand.class, orderId).execute();
        log.info("Order canceled logically {}", order);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping(value = "/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable(value = "orderId") Long orderId) {
        log.info("Received call to GET api/v1/orders/{}", orderId);
        Order order = beanFactory.getBean(GetOrderCommand.class, orderId).execute();
        log.info("Retrieved order {}", order);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PatchMapping(value = "/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable(value = "orderId") Long orderId,
                                             @RequestBody List<ProductQuantity> updateOrderDto) {
        log.info("Received call to PATCH api/v1/orders/{} with request {}", orderId, updateOrderDto);
        Order order = beanFactory.getBean(UpdateOrderCommand.class, orderId, updateOrderDto).execute();
        log.info("Updated order {}: {}", orderId, order);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

}
