package com.microservices.search.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "items")
public class ItemDocument {

    @Id
    private Long itemId;
    private String productId;
    private String productName;
    private String productDescription;
    private Integer quantity;
}
