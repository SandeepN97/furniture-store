package com.example.furniturestore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.multitenant.TenantContext;
import com.example.furniturestore.search.ProductSearchService;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSearchService searchService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) ProductSearchService searchService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.searchService = searchService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(String name, BigDecimal price, String description, String imageUrl, Long categoryId, int stockQuantity) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElse(null);
        }
        Product product = new Product(name, price, description, imageUrl, category, stockQuantity);
        product.setTenantId(TenantContext.getTenantId());
        Product saved = productRepository.save(product);
        if (searchService != null) {
            searchService.indexProduct(saved);
        }
        return saved;
    }

    public Optional<Product> update(Long id, String name, BigDecimal price, String description, String imageUrl, Long categoryId, Integer stockQuantity) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(name);
            existing.setPrice(price);
            existing.setDescription(description);
            existing.setImageUrl(imageUrl);
            Category category = null;
            if (categoryId != null) {
                category = categoryRepository.findById(categoryId).orElse(null);
            }
            existing.setCategory(category);
            if (stockQuantity != null) {
                existing.setStockQuantity(stockQuantity);
            }
            Product updated = productRepository.save(existing);
            if (searchService != null) {
                searchService.indexProduct(updated);
            }
            return updated;
        });
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
