package com.example.marketwithspring.controller;

import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.repository.UserDAO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserDAO userDAO;

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html sahifasini ochadi
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        User user = userDAO.getUserByEmailAndPassword(email, password);

        if (user != null) {
            session.setAttribute("currentUser", user);
            return "redirect:/user-cabinet";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }
}
