package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
