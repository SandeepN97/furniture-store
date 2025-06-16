package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.furniturestore.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
