package com.example.furniturestore.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class BlogPost {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String slug;
    @Lob
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

    public BlogPost() {
    }

    public BlogPost(String title, String slug, String content) {
        this.title = title;
        this.slug = slug;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
