package com.example.furniturestore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.search.ProductDocument;
import com.example.furniturestore.search.ProductSearchService;

@RestController
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public class SearchController {
    private final ProductSearchService searchService;

    public SearchController(ProductSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public List<ProductDocument> search(@RequestParam String q) {
        return searchService.search(q);
    }
}
