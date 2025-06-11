package com.example.marketwithspring.controller.seller;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.repository.OrderDAO;
import com.example.marketwithspring.repository.OrderUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/seller/orders")
public class OrderController {

    private final OrderDAO orderDAO;
    private final OrderUpdater orderUpdater;

    @GetMapping
    public String showOrders(Model model) {
        List<Order> orders = orderDAO.getAllOrders();
        model.addAttribute("orders", orders);
        return "seller/orders";
    }

    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Order order = orderDAO.getOrderById(id);
        if (order != null) {
            orderUpdater.processToConfirmed(id, order.getUser(), order.getProduct(), order.getQuantity(), order.getStatus(), order.getCreatedAt());
            redirectAttributes.addFlashAttribute("message", "Order #" + id + " confirmed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Order #" + id + " not found.");
        }
        return "redirect:/seller/orders";
    }
}