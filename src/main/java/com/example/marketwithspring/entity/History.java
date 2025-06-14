package com.example.marketwithspring.entity;

import lombok.Data;

import java.util.List;

@Data
public class History {
    private Long id;
    private User user;
    private List<Order> orders;
}