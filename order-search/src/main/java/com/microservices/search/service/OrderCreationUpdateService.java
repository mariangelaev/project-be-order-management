package com.microservices.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservices.clients.dto.Product;
import com.microservices.search.adapter.OrderDocumentAdapter;
import com.microservices.search.document.OrderDocument;
import com.microservices.search.model.ItemResource;
import com.microservices.search.model.OrderResource;
import com.microservices.search.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class OrderCreationUpdateService {

    ObjectMapper mapper = new ObjectMapper();

    private final OrderRepository orderRepository;
    private final OrderDocumentAdapter orderDocumentAdapter;
    private final ProductCatalogService productCatalogService;

    @KafkaListener(topics = "ORDERCREATIONUPDATE_TOPIC", groupId = "ORDERCREATIONUPDATE_ENGINE_GROUP_ID")
    public void getMessage(String event) throws JsonProcessingException {

        //fixme: should use JsonDeserializer
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.info("Received message: {}", event);
        OrderResource orderResource = mapper.readValue(event, OrderResource.class);
        log.info("Converted in pojo: {}", orderResource);

        OrderDocument orderDocument = null;
        Optional<OrderDocument> orderDocumentOptional = orderRepository.findById(orderResource.getOrderId());
        if (orderDocumentOptional.isPresent()) {
            orderDocument = orderDocumentOptional.get();
        }

        Map<Long, Product> map = new HashMap<>();
        if (orderResource.getItems() != null) {
            for (ItemResource i : orderResource.getItems()) {
                log.info("Looking for description for product {}", i.getProductId());

                productCatalogService.getProduct(i.getProductId())
                        .ifPresent(p -> map.put(i.getProductId(), p));
            }
        }
        orderDocument = orderDocumentAdapter.adapt(orderDocument, orderResource, map);

        orderRepository.save(orderDocument);

        log.info("Order saved");
    }

}
