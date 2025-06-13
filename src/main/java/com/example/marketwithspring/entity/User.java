package com.example.marketwithspring.entity;


import com.example.marketwithspring.entity.enums.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Double balance;
    private UserRole role;
    private List<Complaint> complaints;
    private History history;
    private List<Order> orders;
    private List<Shop> shops;
    private List<Product> products;
}
