package com.microservices.search.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResource {

    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String orderStatus;
    private List<ItemResource> items;
}
