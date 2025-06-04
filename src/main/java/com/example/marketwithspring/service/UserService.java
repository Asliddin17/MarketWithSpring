package com.example.marketwithspring.service;

import com.example.marketwithspring.repository.ShopDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@RequiredArgsConstructor
@Controller
public class UserService {

    private final ShopDAO shopDAO;

    @GetMapping("/user-cabinet")
    public String home(Model home){return "user-cabinet";}

    @GetMapping("/show-shops")
    public String showAllShops(Model model){
        model.addAttribute("shops", shopDAO.getAllShops());
        return "show-shops";
    }
}
