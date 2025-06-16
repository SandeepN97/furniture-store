package com.example.furniturestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.Subscription;
import com.example.furniturestore.model.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
}
