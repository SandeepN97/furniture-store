package com.example.furniturestore.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.SavedCart;
import com.example.furniturestore.repository.SavedCartRepository;

@RestController
@RequestMapping("/api/cart")
public class SavedCartController {
    private final SavedCartRepository repository;
    public SavedCartController(SavedCartRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/save")
    public Map<String, String> saveCart(@RequestBody Map<String, Object> body) {
        String token = UUID.randomUUID().toString();
        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().valueToTree(body.get("items")).toString();
        repository.save(new SavedCart(token, json));
        return Map.of("token", token);
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getCart(@PathVariable String token) {
        return repository.findByToken(token)
                .map(sc -> {
                    try {
                        java.util.List<?> items = new com.fasterxml.jackson.databind.ObjectMapper().readValue(sc.getItemsJson(), java.util.List.class);
                        return ResponseEntity.ok(Map.of("items", items));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().build();
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
