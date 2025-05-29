package com.microservices.order.command;

import com.microservices.clients.dto.UpdateProductStock;
import com.microservices.order.exception.OutOfStockRuntimeException;
import com.microservices.order.exception.ProductNotInOrderRuntimeException;
import com.microservices.order.exception.StatusOrderRuntimeException;
import com.microservices.order.model.Item;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderStatus;
import com.microservices.order.model.ProductQuantity;
import com.microservices.order.service.ItemService;
import com.microservices.order.service.NotifySearchEngineService;
import com.microservices.order.service.OrderService;
import com.microservices.order.service.ProductCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateOrderCommand extends BaseCommand<Order> {

    private final Long orderId;
    private final List<ProductQuantity> updateOrder;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductCatalogService productCatalogService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private NotifySearchEngineService notifySearchEngineService;

    @Override
    public Order execute() {

        Order order = orderService.getOrder(orderId);
        if (OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            throw new StatusOrderRuntimeException();
        }

        List<Item> items = order.getItems();

        Map<Long, Item> itemEntityMap = items.stream()
                .collect(Collectors.toMap(Item::getProductId, item -> item));

        if (!productCatalogService.updateStock(getDeltaQuantities(itemEntityMap))) {
            throw new OutOfStockRuntimeException();
        }

        for (ProductQuantity newPQ : updateOrder) {
            if (itemEntityMap.containsKey(newPQ.getProductId())) {
                itemService.updateQuantity(itemEntityMap.get(newPQ.getProductId()).getItemId(), newPQ.getQuantity());
            }
        }
        order = orderService.getOrder(orderId);
        notifySearchEngineService.sendOrder(order);

        return order;
    }

    private List<UpdateProductStock> getDeltaQuantities(Map<Long, Item> itemMap) {
        List<UpdateProductStock> quantityDtos = new ArrayList<>();

        for (ProductQuantity newPQ : updateOrder) {
            Long currentProductId = newPQ.getProductId();

            if (itemMap.containsKey(currentProductId)) {
                Integer currentProductQuantity = itemMap.get(currentProductId).getQuantity();
                if (newPQ.getQuantity() != currentProductQuantity) {
                    quantityDtos.add(new UpdateProductStock(currentProductId, newPQ.getQuantity() - currentProductQuantity));
                }
            } else {
                throw new ProductNotInOrderRuntimeException(currentProductId);
            }
        }
        return quantityDtos;
    }
}
