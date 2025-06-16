package com.example.furniturestore.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
