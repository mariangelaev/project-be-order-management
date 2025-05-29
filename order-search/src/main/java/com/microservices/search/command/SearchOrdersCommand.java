package com.microservices.search.command;

import com.microservices.search.document.OrderDocument;
import com.microservices.search.repository.OrderRepository;
import com.microservices.search.service.SearchOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class SearchOrdersCommand implements BaseCommand<Page<OrderDocument>> {

    private final String productName;
    private final String productDescription;
    private final String statusOrder;
    private final LocalDate creationDate;
    private final String page;
    private final String size;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SearchOrderService searchOrderService;

    @Override
    public Page<OrderDocument> execute() {
        return searchOrderService.search(productName,
                productDescription,
                statusOrder,
                creationDate,
                PageRequest.of(Integer.parseInt(page), Integer.parseInt(size)));
    }
}
