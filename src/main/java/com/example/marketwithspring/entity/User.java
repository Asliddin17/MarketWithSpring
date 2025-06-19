package com.example.marketwithspring.entity;


import com.example.marketwithspring.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
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

    public User(String name, String email, String password, Double balance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
}

