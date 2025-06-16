package com.example.furniturestore.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
  
    @Query("select sum(o.totalPrice) from Order o")
    BigDecimal sumTotalPrice();
}
