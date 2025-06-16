package com.example.furniturestore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public List<Product> all() {
        return repository.findAll();
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
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody CreateProductRequest request) {
        Category category = null;
        if (request.categoryId != null) {
            category = categoryRepository.findById(request.categoryId).orElse(null);
        }
        Product product = new Product(request.name, request.price, request.description,
                request.imageUrl, category);
        Product saved = repository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload-image")
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
