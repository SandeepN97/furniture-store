package com.example.furniturestore.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(String message) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "AI service not configured";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", Collections.singletonList(Map.of("role", "user", "content", message))
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", entity, Map.class);
        if (response == null) return "";
        Object choices = ((java.util.List<?>) response.get("choices")).get(0);
        if (choices instanceof Map<?, ?> choice) {
            Map<?, ?> msg = (Map<?, ?>) choice.get("message");
            return msg.get("content").toString();
        }
        return "";
    }
}
