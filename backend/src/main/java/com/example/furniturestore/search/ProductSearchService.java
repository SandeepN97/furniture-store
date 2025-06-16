package com.example.furniturestore.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import com.example.furniturestore.model.Product;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public class ProductSearchService {
    private final ProductSearchRepository repository;
    private final ElasticsearchOperations operations;

    public ProductSearchService(ProductSearchRepository repository, ElasticsearchOperations operations) {
        this.repository = repository;
        this.operations = operations;
    }

    public void indexProduct(Product product) {
        ProductDocument doc = new ProductDocument(product.getId(), product.getName(), product.getDescription(),
                product.getCategory() != null ? product.getCategory().getName() : null, product.getPrice());
        repository.save(doc);
    }

    public List<ProductDocument> search(String query) {
        StringQuery sq = new StringQuery("{" +
                "\"query\": { \"query_string\": { \"query\": \"" + query + "\" } } }");
        SearchHits<ProductDocument> hits = operations.search(sq, ProductDocument.class);
        return hits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());
    }
}
