package com.microservices.order.command;

import com.microservices.order.model.Order;
import com.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class GetOrderCommand extends BaseCommand<Order> {

    private final Long orderId;

    @Autowired
    private OrderService orderService;

    @Override
    public Order execute() {
        return orderService.getOrder(orderId);
    }
}
