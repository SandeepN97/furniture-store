package com.example.furniturestore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.furniturestore.model.Product;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> search(@Param("tenantId") String tenantId, @Param("categoryId") Long categoryId,
            @Param("name") String name, Pageable pageable);

    Optional<Product> findByIdAndTenantId(Long id, String tenantId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
