package com.example.furniturestore.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.ReturnRequest;
import com.example.furniturestore.model.ReturnStatus;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ReturnRequestRepository;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {
    private final ReturnRequestRepository returnRepository;
    private final OrderRepository orderRepository;

    public ReturnController(ReturnRequestRepository returnRepository, OrderRepository orderRepository) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
    }

    public static class CreateRequest {
        public Long orderId;
        public String reason;
    }

    @PostMapping
    public ReturnRequest requestReturn(@RequestBody CreateRequest request) {
        Order order = orderRepository.findById(request.orderId).orElseThrow();
        ReturnRequest rr = new ReturnRequest();
        rr.setOrder(order);
        rr.setReason(request.reason);
        return returnRepository.save(rr);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnRequest> list() {
        return returnRepository.findAll();
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnRequest refund(@PathVariable Long id) {
        ReturnRequest rr = returnRepository.findById(id).orElseThrow();
        rr.setStatus(ReturnStatus.REFUNDED);
        return returnRepository.save(rr);
    }
}
