package com.example.marketwithspring.entity;

import lombok.Data;

import java.util.List;
@Data

public class Shop {
    private Long id;
    private String name;
    private List<Product> products;
}
