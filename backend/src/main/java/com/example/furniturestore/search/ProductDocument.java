package com.example.furniturestore.search;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products")
public class ProductDocument {
    @Id
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;

    public ProductDocument() {}

    public ProductDocument(Long id, String name, String description, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
}
