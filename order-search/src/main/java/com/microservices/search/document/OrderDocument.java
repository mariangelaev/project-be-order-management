package com.microservices.search.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "orders")
public class OrderDocument {

    @Id
    private Long orderId;
    @Field(type = FieldType.Date)
    private LocalDate createdAt;
    @Field(type = FieldType.Date)
    private LocalDate updatedAt;
    private String orderStatus;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<ItemDocument> items;
}
