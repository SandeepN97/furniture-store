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
import com.example.furniturestore.multitenant.TenantContext;

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
                             Pageable pageable) {
        return repository.search(TenantContext.getTenantId(), categoryId, name, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> byId(@PathVariable Long id) {
        return repository.findByIdAndTenantId(id, TenantContext.getTenantId())
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
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody CreateProductRequest request) {
        Category category = null;
        if (request.categoryId != null) {
            category = categoryRepository.findById(request.categoryId).orElse(null);
        }
        int qty = request.stockQuantity != null ? request.stockQuantity : 0;
        Product product = new Product(request.name, request.price, request.description,
                request.imageUrl, category, qty);
        product.setTenantId(TenantContext.getTenantId());
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
            if (request.stockQuantity != null) {
                existing.setStockQuantity(request.stockQuantity);
            }
            existing.setTenantId(TenantContext.getTenantId());
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

    @PostMapping("/upload-csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                String name = parts[0].trim();
                BigDecimal price = new BigDecimal(parts[1].trim());
                String description = parts[2].trim();
                Long catId = Long.parseLong(parts[3].trim());
                int qty = Integer.parseInt(parts[4].trim());
                Category cat = categoryRepository.findById(catId).orElse(null);
                Product p = new Product(name, price, description, null, cat, qty);
                p.setTenantId(TenantContext.getTenantId());
                repository.save(p);
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CSV upload failed");
        }
    }
}
