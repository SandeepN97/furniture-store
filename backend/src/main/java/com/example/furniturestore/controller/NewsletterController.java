package com.example.furniturestore.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.EmailSubscriber;
import com.example.furniturestore.repository.EmailSubscriberRepository;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final EmailSubscriberRepository repo;

    public NewsletterController(EmailSubscriberRepository repo) {
        this.repo = repo;
    }

    public static class SignupRequest {
        public String email;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> signup(@RequestBody SignupRequest request) {
        repo.save(new EmailSubscriber(request.email));
        return Map.of("status", "subscribed");
    }
}
