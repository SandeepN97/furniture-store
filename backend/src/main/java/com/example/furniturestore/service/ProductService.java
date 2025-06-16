package com.example.furniturestore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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
        return productRepository.save(product);
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
            return productRepository.save(existing);
        });
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
