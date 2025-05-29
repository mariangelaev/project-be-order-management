package com.microservices.search.service;

import com.microservices.search.document.OrderDocument;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static java.util.Objects.nonNull;
import static org.springframework.data.elasticsearch.core.SearchHitSupport.searchPageFor;
import static org.springframework.data.elasticsearch.core.SearchHitSupport.unwrapSearchHits;

@Service
@AllArgsConstructor
public class SearchOrderService {

    private final ElasticsearchOperations elasticsearchOperations;

    @SuppressWarnings("unchecked")
    public Page<OrderDocument> search(String productName,
                                      String productDescription,
                                      String statusOrder,
                                      LocalDate creationDate,
                                      Pageable pageable) {
        return (Page<OrderDocument>) unwrapSearchHits(searchPageFor(searchHits(productName, productDescription, statusOrder, creationDate, pageable), pageable));
    }

    private SearchHits<OrderDocument> searchHits(String productName,
                                         String productDescription,
                                         String statusOrder,
                                         LocalDate creationDate,
                                         Pageable pageable) {
        CriteriaQuery query = buildSearchQuery(productName, productDescription, statusOrder, creationDate);
        query.setPageable(pageable);

        return elasticsearchOperations.search(query, OrderDocument.class);
    }

    private CriteriaQuery buildSearchQuery(String productName,
                                           String productDescription,
                                           String statusOrder,
                                           LocalDate creationDate) {
        var criteria = new Criteria();
        if (nonNull(statusOrder)) {
            criteria.and(new Criteria("orderStatus").is(statusOrder));
        }
        if (nonNull(productDescription)) {
            criteria.and(new Criteria("items.productDescription").contains(productDescription));
        }
        if (nonNull(productName)) {
            criteria.and(new Criteria("items.productName").contains(productName));
        }
        if (nonNull(creationDate)) {
            criteria.and(new Criteria("createdAt").is(creationDate));
        }
        return new CriteriaQuery(criteria);
    }
}
