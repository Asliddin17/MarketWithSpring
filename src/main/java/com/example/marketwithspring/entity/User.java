package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.UserRole;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private Complaint complaint;
    private History history;
    private Order order;
    private Shop shop;
    private Product product;
}
