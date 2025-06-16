package com.example.furniturestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.SavedCart;

public interface SavedCartRepository extends JpaRepository<SavedCart, Long> {
    Optional<SavedCart> findByToken(String token);
}
