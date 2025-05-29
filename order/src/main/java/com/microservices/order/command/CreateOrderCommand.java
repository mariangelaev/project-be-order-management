package com.microservices.order.command;

import com.microservices.clients.dto.UpdateProductStock;
import com.microservices.order.exception.OutOfStockRuntimeException;
import com.microservices.order.model.CreateOrder;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class CreateOrderCommand extends BaseCommand<Order> {

    private final CreateOrder request;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductCatalogService productCatalogService;
    @Autowired
    private NotifySearchEngineService notifySearchEngineService;

    @Override
    public Order execute() {

        Order order = orderService.createOrder(request.getCustomerId(), request.getProductQuantities());
        notifySearchEngineService.sendOrder(order);

        if (!productCatalogService.updateStock(getProductStocks(request.getProductQuantities()))) {
            order = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.CANCELED);
            notifySearchEngineService.sendOrder(order);
            throw new OutOfStockRuntimeException();
        }

        order = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.CONFIRMED);
        notifySearchEngineService.sendOrder(order);
        return order;
    }

    private List<UpdateProductStock> getProductStocks(List<ProductQuantity> productQuantities) {
        return productQuantities.stream()
                .map(this::getProductStock)
                .collect(Collectors.toList());
    }

    private UpdateProductStock getProductStock(ProductQuantity productQuantity) {
        UpdateProductStock updateProductStock = new UpdateProductStock();
        updateProductStock.setProductId(productQuantity.getProductId());
        updateProductStock.setQuantity(-productQuantity.getQuantity());
        return updateProductStock;
    }
}
