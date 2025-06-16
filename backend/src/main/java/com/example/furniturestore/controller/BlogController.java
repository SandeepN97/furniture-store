package com.example.furniturestore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.BlogPost;
import com.example.furniturestore.repository.BlogPostRepository;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogPostRepository blogPostRepository;

    public BlogController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @GetMapping
    public List<BlogPost> list() {
        return blogPostRepository.findAll();
    }

    @GetMapping("/{slug}")
    public BlogPost getBySlug(@PathVariable String slug) {
        return blogPostRepository.findBySlug(slug).orElseThrow();
    }
}
