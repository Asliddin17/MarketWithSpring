package com.example.marketwithspring.service;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserService {

    private final UserShopDAO userShopDAO;
    private final UserProductDAO userProductDAO;
    private final UserOrderDAO userOrderDAO;
    private final UserHistoryDAO userHistoryDAO;
    private final OrderService orderService;
    private final UserDAO userDAO;

    @GetMapping("/user-cabinet")
    public String home(Model home) {
        return "user-cabinet";
    }

    @GetMapping("/show-shops")
    public String showAllShops(Model model) {
        model.addAttribute("shops", userShopDAO.getAllShops());
        return "show-shops";
    }

    @GetMapping("/buy-product")
    public String buyProduct(Model model) {
        model.addAttribute("shops", userShopDAO.getAllShops());
        model.addAttribute("products", userProductDAO.getAllProducts());
        return "show-products";
    }

    @GetMapping("/buy-product/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        Product product = userProductDAO.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @PostMapping("/order/{id}")
    public String buyingProduct(@PathVariable("id") Long id, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("currentUser");
        System.out.println("Current user: " + user);
        Product product = userProductDAO.getProductById(id);

        if (user == null || user.getBalance() == null) {
            return "index";
        }

        if (product.getCount() > 0 && user.getBalance().compareTo(product.getPrice()) >= 0) {
            String historyIdByUser = userHistoryDAO.getOrCreateHistoryIdByUser(user);
            Long orderId = userOrderDAO.addProductToOrderList(product, user, historyIdByUser);
            System.out.println("Order ID created: " + orderId);
            // Faqat buyurtma yaratamiz, lekin hali balansdan pul yechmaymiz va mahsulot kamaytirmaymiz
            model.addAttribute("order", userOrderDAO.getOrderById(orderId));
            return "confirm-purchase"; // Bu sahifada tasdiqlash bo'ladi
        } else {
            model.addAttribute("error", "Yetarli mahsulot yoki balans mavjud emas.");
            model.addAttribute("product", product);
            return "product-detail";
        }
    }


    @PostMapping("/confirm-purchase/{orderId}")
    public String confirmPurchase(@PathVariable("orderId") Long orderId, Model model) {
        Order order = userOrderDAO.getOrderById(orderId);
        Product product = userProductDAO.getProductById(order.getProduct().getId());
        User user = (User) model.getAttribute("currentUser");

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            // Only now reduce quantity and deduct balance
            if (product.getCount() > 0 && user.getBalance().compareTo(product.getPrice()) >= 0) {
                userProductDAO.decreaseProductCount(product.getId());
                userDAO.deductUserBalance(user.getId(), product.getPrice());
                userHistoryDAO.addToHistory(user, product, orderId);
                return "redirect:/order-success";
            } else {
                model.addAttribute("error", "Insufficient stock or balance.");
                return "order-failure";
            }
        } else {
            model.addAttribute("error", "Order not confirmed by seller yet.");
            return "wait-for-confirmation";
        }
    }


    @PostMapping("/confirm-order/{orderId}")
    public String confirmOrder(@PathVariable Long orderId){
        orderService.confirmOrder(orderId);
        return "order-list";
    }


    @GetMapping("/my-products")
    public String showMyProducts(Model model){
        model.addAttribute("myproducts", userProductDAO.getAllProductsWithConfirmedStatus());
        return "my-products";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model){
        List<Order> allOrders = userOrderDAO.getAllOrders();
        model.addAttribute("orders", allOrders);
        return "my-orders";
    }

}
