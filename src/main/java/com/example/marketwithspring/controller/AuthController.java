package com.example.marketwithspring.controller;

import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.UserRole;
import com.example.marketwithspring.repository.UserDAO;
import com.example.marketwithspring.repository.UserProductDAO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RequiredArgsConstructor
@Controller
public class AuthController {
    private final UserDAO userDAO;
    @GetMapping("/")
    public String home(Model model){
        return "index";
    }


    @GetMapping("sign-up")
    public String signUp(){
        return "sign-up";
    }

    @PostMapping("sign-up")
    public String signUp(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam("balance") Double balance,
                         Model model){
        User user = new User(name, email, password, balance);
        User userByEmailAndPassword = userDAO.getUserByEmailAndPassword(email, password);
        if (userByEmailAndPassword == null){
            userDAO.saveUser(user);
        }else {
            return "error";
        }
        return "sign-in";
    }

    @GetMapping("sign-in")
    public String signIn(){
        return "sign-in";
    }

    @PostMapping("sign-in")
    public String signIn(@RequestParam("email") String email,
                         @RequestParam("password") String password,
                         HttpSession session,
                         Model model){
        User user = userDAO.getUserByEmailAndPassword(email, password);
        if(user == null){
            return "error";
        }
        session.setAttribute("currentUser", user);
        if(user.getRole().equals(UserRole.ADMIN)){
            return "admin/admin";
        } else if (user.getRole().equals(UserRole.SELLER)) {
            return "seller/crud-shop/seller-cabinet";
        }else {
            model.addAttribute("currentUser", user);
            return "user/user-cabinet";
        }
    }
}
