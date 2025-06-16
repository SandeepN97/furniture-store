package com.example.furniturestore.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;


import com.example.furniturestore.model.ApiKey;
import com.example.furniturestore.repository.ApiKeyRepository;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ApiKeyService(ApiKeyRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> create(String name) {
        String key = UUID.randomUUID().toString();
        String prefix = key.substring(0, 8);
        String hash = passwordEncoder.encode(key);
        ApiKey apiKey = new ApiKey(name, prefix, hash);
        repository.save(apiKey);
        return Map.of(
            "name", name,
            "prefix", prefix,
            "key", key
        );
    }

    public boolean isValid(String key) {
        String prefix = key.substring(0, 8);
        return repository.findByPrefix(prefix)
                .map(stored -> passwordEncoder.matches(key, stored.getKeyHash()))
                .orElse(false);
    }
}
