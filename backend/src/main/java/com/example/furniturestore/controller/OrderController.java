package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.OrderItem;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ProductRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public static class ItemRequest {
        public Long productId;
        public int quantity;
    }

    public static class CreateOrderRequest {
        public String customerName;
        public List<ItemRequest> items;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderRequest request) {
        if (request == null || request.items == null || request.items.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Order order = new Order();
        order.setCustomerName(request.customerName);
        for (ItemRequest ir : request.items) {
            Product product = productRepository.findById(ir.productId).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().build();
            }
            OrderItem item = new OrderItem(product, ir.quantity, product.getPrice());
            item.setOrder(order);
            order.getItems().add(item);
        }
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }
}
