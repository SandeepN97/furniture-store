package com.example.furniturestore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public ProductController(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public Page<Product> all(@RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) BigDecimal minPrice,
                             @RequestParam(required = false) BigDecimal maxPrice,
                             @RequestParam(required = false) Boolean inStock,
                             Pageable pageable) {
        // Use advanced search if more filters are provided
        if (search != null || minPrice != null || maxPrice != null || inStock != null) {
            return repository.advancedSearch(categoryId, search, minPrice, maxPrice, inStock, pageable);
        }
        return repository.search(categoryId, name, pageable);
    }

    @GetMapping("/featured")
    public ResponseEntity<java.util.List<Product>> getFeatured() {
        return ResponseEntity.ok(repository.findByIsFeaturedTrue());
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<java.util.List<Product>> getRelatedProducts(@PathVariable Long id) {
        Product product = repository.findById(id).orElse(null);
        if (product == null || product.getCategory() == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        org.springframework.data.domain.PageRequest pageRequest =
            org.springframework.data.domain.PageRequest.of(0, 4);
        java.util.List<Product> related = repository.findRelatedProducts(
            product.getCategory().getId(), id, pageRequest);
        return ResponseEntity.ok(related);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(repository.findLowStockProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> byId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public static class CreateProductRequest {
        public String name;
        public BigDecimal price;
        public String description;
        public String imageUrl;
        public Long categoryId;
        public Integer stockQuantity;
        public Boolean isFeatured;
        public String sku;
        public Double weight;
        public String dimensions;
        public String material;
        public String color;
        public Integer lowStockThreshold;
        public java.util.List<String> additionalImages;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody CreateProductRequest request) {
        Category category = null;
        if (request.categoryId != null) {
            category = categoryRepository.findById(request.categoryId).orElse(null);
        }
        Product product = new Product(request.name, request.price, request.description,
                request.imageUrl, category);

        // Set additional fields
        if (request.stockQuantity != null) {
            product.setStockQuantity(request.stockQuantity);
        }
        if (request.isFeatured != null) {
            product.setIsFeatured(request.isFeatured);
        }
        product.setSku(request.sku);
        product.setWeight(request.weight);
        product.setDimensions(request.dimensions);
        product.setMaterial(request.material);
        product.setColor(request.color);
        if (request.lowStockThreshold != null) {
            product.setLowStockThreshold(request.lowStockThreshold);
        }
        if (request.additionalImages != null) {
            product.setAdditionalImages(request.additionalImages);
        }

        Product saved = repository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody CreateProductRequest request) {
        return repository.findById(id).map(existing -> {
            existing.setName(request.name);
            existing.setPrice(request.price);
            existing.setDescription(request.description);
            if (request.categoryId != null) {
                Category cat = categoryRepository.findById(request.categoryId).orElse(null);
                existing.setCategory(cat);
            } else {
                existing.setCategory(null);
            }
            existing.setImageUrl(request.imageUrl);

            // Update additional fields
            if (request.stockQuantity != null) {
                existing.setStockQuantity(request.stockQuantity);
            }
            if (request.isFeatured != null) {
                existing.setIsFeatured(request.isFeatured);
            }
            existing.setSku(request.sku);
            existing.setWeight(request.weight);
            existing.setDimensions(request.dimensions);
            existing.setMaterial(request.material);
            existing.setColor(request.color);
            if (request.lowStockThreshold != null) {
                existing.setLowStockThreshold(request.lowStockThreshold);
            }
            if (request.additionalImages != null) {
                existing.setAdditionalImages(request.additionalImages);
            }

            Product saved = repository.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File required");
        }
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir).resolve(filename);
            file.transferTo(path);
            return Map.of("url", "/uploads/" + filename);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
    }
}
