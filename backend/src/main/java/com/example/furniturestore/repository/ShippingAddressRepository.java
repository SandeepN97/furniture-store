package com.example.furniturestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.ShippingAddress;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    List<ShippingAddress> findByUserId(Long userId);

    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(Long userId);
}
