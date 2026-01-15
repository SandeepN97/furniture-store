package com.example.furniturestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findTop10ByOrderByOrderDateDesc();

    @Query("SELECT MONTH(o.orderDate) as month, YEAR(o.orderDate) as year, " +
           "SUM(o.totalPrice) as revenue, COUNT(o) as orderCount " +
           "FROM Order o WHERE o.orderDate >= :startDate AND o.status != 'CANCELLED' " +
           "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
           "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> getMonthlyRevenue(@Param("startDate") LocalDateTime startDate);

    List<Order> findByUserId(Long userId);
}
