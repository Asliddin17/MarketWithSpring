package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.CommentStatus;
import lombok.Data;

@Data
public class Comment {
    private String id;
    private String userId;
    private String productId;
    private String commentText;
    private CommentStatus status;
}