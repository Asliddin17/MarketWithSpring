package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.CommentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Complaint {
    private Long id;
    private String reason;
    private User filedBy;
    private CommentStatus status; // Kim complaint yubordi
    private Product ratingProduct;  // Qaysi productga complaint
    private LocalDateTime createdAt;
}
