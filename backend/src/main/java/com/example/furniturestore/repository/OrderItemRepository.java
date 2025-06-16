package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
