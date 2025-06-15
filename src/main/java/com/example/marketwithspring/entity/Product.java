package com.example.marketwithspring.entity;

import com.example.marketwithspring.entity.enums.ProductStatus;
import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private double price;
    private Long orderId;
    private Shop shop;
    private Integer ratings;
    private Integer count;
    private ProductStatus productStatus;
}
