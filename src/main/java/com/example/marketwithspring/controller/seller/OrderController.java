package com.example.marketwithspring.controller.seller;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.repository.OrderDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/seller/orders")
public class OrderController {

    private final OrderDAO orderDAO;

    @GetMapping
    public String showOrders(Model model) {
        model.addAttribute("orders", orderDAO.getAllOrders());
        return "seller/orders";
    }

    // Display form to update order status
    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model) {
        Order order = orderDAO.getOrderById(id);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "redirect:/seller/orders";
        }
        model.addAttribute("order", order);
        return "seller/order-edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam OrderStatus status, Model model) {
        Order order = orderDAO.getOrderById(id);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "redirect:/seller/orders";
        }

        if (order.getStatus() == OrderStatus.PROCESSING) {
            order.setStatus(status);
            orderDAO.updateOrder(order);
            model.addAttribute("message", "Order status updated to " + status);
        } else {
            model.addAttribute("error", "Status can only be updated when order is PROCESSING");
        }

        return "redirect:/seller/orders";
    }
}