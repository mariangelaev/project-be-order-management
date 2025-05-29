package com.microservices.order.service;

import com.microservices.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotifySearchEngineService {

    @Autowired
    private KafkaTemplate<String, Order> producer;

    public void sendOrder(Order event) {
        log.info("Sending event");
        producer.send("ORDERCREATIONUPDATE_TOPIC", event);
    }
}
