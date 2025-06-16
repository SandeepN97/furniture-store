package com.example.furniturestore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.furniturestore.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> search(@Param("categoryId") Long categoryId, @Param("name") String name,
            Pageable pageable);
}
