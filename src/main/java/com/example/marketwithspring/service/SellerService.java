package com.example.marketwithspring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {
    public String showSellerCabinet() {
        return "seller/crud-shop/seller-cabinet";
    }
}