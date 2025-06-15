package com.example.marketwithspring.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Complaint {
    private Long id;
    private String reason;
    private Integer rating;
    private User filedBy;
    private Product ratingProduct;  // Qaysi productga complaint
    private LocalDateTime createdAt;
}
