package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
