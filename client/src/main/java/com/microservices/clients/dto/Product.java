package com.microservices.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private Long productId;
    private String productName;
    private String productDescription;
    private Integer productStock;
    private String productCategory;
}
