package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderUpdater {

    private final JdbcTemplate jdbcTemplate;

    public OrderUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void processToConfirmed(Long id, User user, Product product, Integer quantity, OrderStatus status, LocalDateTime createdAt) {
        String sql = "UPDATE order_table SET status = 'CONFIRMED' WHERE id = ? AND status = 'PROCESSING'";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected > 0) {
            System.out.println("Order #" + id + " updated to CONFIRMED successfully.");
        } else {
            System.out.println("No order with ID " + id + " found in PROCESSING status.");
        }
    }
}