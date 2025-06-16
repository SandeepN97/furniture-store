package com.example.furniturestore.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.furniturestore.model.ApiKey;
import com.example.furniturestore.repository.ApiKeyRepository;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public ApiKey create(String name) {
        String key = UUID.randomUUID().toString();
        ApiKey apiKey = new ApiKey(name, key);
        return repository.save(apiKey);
    }

    public boolean isValid(String key) {
        return repository.findByKey(key).isPresent();
    }
}
