package com.example.marketwithspring.controller.seller;

import com.example.marketwithspring.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @GetMapping("/seller-cabinet")
    public String showSellerCabinet() {
        return sellerService.showSellerCabinet();
    }
}