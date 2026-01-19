package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByBusinessId(Long businessId);

    List<Product> findByBusinessIdAndActiveTrue(Long businessId);

    List<Product> findByNameContainingIgnoreCase(String name);
}
