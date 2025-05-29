package com.microservices.order.command;

import com.microservices.clients.dto.UpdateProductStock;
import com.microservices.order.exception.ProductCatalogRuntimeException;
import com.microservices.order.exception.StatusOrderRuntimeException;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class DeleteOrderCommand extends BaseCommand<Order> {

    private final Long orderId;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductCatalogService productCatalogService;
    @Autowired
    private NotifySearchEngineService notifySearchEngineService;

    @Override
    @Transactional
    public Order execute() {
        Order order = orderService.getOrder(orderId);
        if (!OrderStatus.CONFIRMED.equals(order.getOrderStatus())) {
            throw new StatusOrderRuntimeException();
        }

        List<UpdateProductStock> productQuantityList = order.getItems()
                .stream()
                .map(i -> new UpdateProductStock(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        if (!productCatalogService.updateStock(productQuantityList)) {
            throw new ProductCatalogRuntimeException();
        }

        order = orderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
        notifySearchEngineService.sendOrder(order);
        return order;
    }
}
