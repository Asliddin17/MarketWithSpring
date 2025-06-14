package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.CommentStatus;
import lombok.Data;

@Data
public class Comment {
    private String id;
    private User userId;
    private Product productId;
    private String commentText;
    private CommentStatus status;
}