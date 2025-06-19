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
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserDAO userDAO;


    @GetMapping("/login")
    public String loginForm() {
        return "user/login"; // login.html sahifasini ochadi
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
            return "user/login";
        }
    }

    @GetMapping("/user-cabinet")
    public String userCabinet(@SessionAttribute("currentUser") User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
        return "user/user-cabinet";
    }
}
