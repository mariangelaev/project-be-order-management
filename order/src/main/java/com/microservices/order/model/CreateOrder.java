package com.microservices.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrder {

    private Long customerId;
    private List<ProductQuantity> productQuantities;
}
