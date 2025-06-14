package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

public class Order {
    private Long id;
    private User user;
    private Product product;
    private Integer quantity;
    private OrderStatus status;
    private History historyId;
    private LocalDateTime createdAt;
}