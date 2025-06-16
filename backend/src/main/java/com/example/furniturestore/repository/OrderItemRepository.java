package com.example.furniturestore.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.furniturestore.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    interface TopProductProjection {
        String getName();
        Long getQuantity();
    }

    @Query("select oi.product.name as name, sum(oi.quantity) as quantity from OrderItem oi group by oi.product.name order by quantity desc")
    List<TopProductProjection> findTopProducts(Pageable pageable);
}
