package com.example.marketwithspring.controller;

<<<<<<< HEAD
import com.example.marketwithspring.repository.UserProductDAO;
import lombok.RequiredArgsConstructor;
=======

>>>>>>> 99d2157 (second commit)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home(Model model){
        return "index";
    }

}
