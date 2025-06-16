package com.example.furniturestore.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class EmailSubscriber {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private LocalDateTime subscribedAt = LocalDateTime.now();

    public EmailSubscriber() {}

    public EmailSubscriber(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }
}
