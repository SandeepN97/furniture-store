package com.example.furniturestore.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.Product;

@Service
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyNewOrder(Order order) {
        messagingTemplate.convertAndSend("/topic/orders", java.util.Map.of("id", order.getId()));
    }

    public void notifyLowStock(Product product) {
        messagingTemplate.convertAndSend("/topic/low-stock", product);
    }
}
