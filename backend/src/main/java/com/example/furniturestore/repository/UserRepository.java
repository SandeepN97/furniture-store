package com.example.furniturestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.furniturestore.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
