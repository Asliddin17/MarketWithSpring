package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryDTO {
    private Product product;
    private OrderStatus orderStatus;
    private LocalDateTime dateTime;
}
