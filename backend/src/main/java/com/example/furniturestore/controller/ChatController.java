package com.example.furniturestore.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.service.ChatService;

@RestController
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = chatService.ask(message);
        return Map.of("reply", reply);
    }
}
