package com.example.marketwithspring.entity;

import java.time.LocalDateTime;

public class Complaint {
    private Long id;
    private String reason;
    private User filedBy;           // Kim complaint yubordi
    private Product targetProduct;  // Qaysi productga complaint
    private LocalDateTime createdAt;
}
