package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.furniturestore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
