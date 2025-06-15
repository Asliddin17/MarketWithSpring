package com.example.marketwithspring.service;

import com.example.marketwithspring.entity.Complaint;
import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.CommentStatus;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserService {

    private final UserShopDAO userShopDAO;
    private final UserProductDAO userProductDAO;
    private final UserOrderDAO userOrderDAO;
    private final UserHistoryDAO userHistoryDAO;
//    private final OrderService orderService;
    private final UserDAO userDAO;
    private final ComplaintServiceDAO complaintServiceDAO;

//    @GetMapping("/user-cabinet")
//    public String home(Model home) {
//        return "user/user-cabinet";
//    }

    @GetMapping("/show-shops")
    public String showAllShops(Model model) {
        model.addAttribute("shops", userShopDAO.getAllShops());
        return "user/show-shops";
    }

    @GetMapping("/buy-product")
    public String buyProduct(Model model) {
        model.addAttribute("shops", userShopDAO.getAllShops());
        model.addAttribute("products", userProductDAO.getAllProducts());
        return "user/show-products";
    }

    @GetMapping("/buy-product/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model) {
        Product product = userProductDAO.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("comments", complaintServiceDAO.getAllCommentsRelatedToProduct(id));
        model.addAttribute("avg", complaintServiceDAO.getAvgRatingForProduct(id));
        return "user/product-detail";
    }

    @PostMapping("/order/{id}")
    public String buyingProduct(@PathVariable("id") Long id, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("currentUser");
        System.out.println("Current user: " + user);
        Product product = userProductDAO.getProductById(id);

        if (user == null || user.getBalance() == null) {
            return "user/index";
        }

        if (product.getCount() > 0 && user.getBalance().compareTo(product.getPrice()) >= 0) {
            String historyIdByUser = userHistoryDAO.getOrCreateHistoryIdByUser(user);
            Long orderId = userOrderDAO.addProductToOrderList(product, user, historyIdByUser);
            System.out.println("Order ID created: " + orderId);
            // Faqat buyurtma yaratamiz, lekin hali balansdan pul yechmaymiz va mahsulot kamaytirmaymiz
            model.addAttribute("order", userOrderDAO.getOrderById(orderId));
            return "user/confirm-purchase"; // Bu sahifada tasdiqlash bo'ladi
        } else {
            model.addAttribute("error", "Yetarli mahsulot yoki balans mavjud emas.");
            model.addAttribute("product", product);
            return "user/product-detail";
        }
    }


    @PostMapping("/confirm-purchase/{orderId}")
    public String confirmPurchase(@PathVariable("orderId") Long orderId, HttpSession httpSession, Model model) {
        Order order = userOrderDAO.getOrderById(orderId);
        Product product = userProductDAO.getProductById(order.getProduct().getId());
        User user = (User) httpSession.getAttribute("currentUser");

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            if (product.getCount() > 0 && user.getBalance().compareTo(product.getPrice()) >= 0) {
                userProductDAO.decreaseProductCount(product.getId());
                userDAO.deductUserBalance(user.getId(), product.getPrice());
                User updatedUser = userDAO.getUserById(user.getId());
                httpSession.setAttribute("currentUser", updatedUser);
                userHistoryDAO.addToHistory(user, product, orderId);

                // ✅ Qo'shish: agar hali user_product yo‘q bo‘lsa yoki avval cancel bo‘lsa ham yangilasin
//                userProductDAO.addToUserProductTable(user.getId(), product.getId());

                return "redirect:/user-cabinet";
            } else {
                model.addAttribute("error", "Insufficient stock or balance.");
                return "redirect:/confirm-purchase?error=notavailableproduct";
            }
        } else {
            model.addAttribute("error", "Order not confirmed by seller yet.");
            return "user/wait-for-confirmation";
        }
    }




//    @PostMapping("/confirm-order/{orderId}")
//    public String confirmOrder(@PathVariable Long orderId){
//        orderService.confirmOrder(orderId);
//        return "user/order-list";
//    }


    @GetMapping("/my-products")
    public String showMyProducts(HttpSession session, Model model){
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("myproducts", userProductDAO.getAllProductsWithConfirmedStatus(currentUser.getId()));
        return "user/my-products";
    }

    ///////////////////////////////////////////
    @PostMapping("/my-products/{id}")
    public String showMyProducts(@PathVariable("id")Long id, HttpSession session, Model model){
        User currentUser = (User)session.getAttribute("currentUser");
        userProductDAO.cancelOrder(id);
        Product product = userProductDAO.getProductByOrderId(id);
        if (product == null) {
            return "redirect:/my-products?error=productnotfound";
        }
        userDAO.refundBalanceToUser(currentUser, product.getPrice());
        User updatedUser = userDAO.getUserById(currentUser.getId());
        session.setAttribute("currentUser", updatedUser);
        userProductDAO.increaseProductCount(id);
        return "redirect:/user-cabinet";
    }


    @GetMapping("/my-orders")
    public String showMyOrders(Model model){
        List<Order> allOrders = userOrderDAO.getAllOrders();
        model.addAttribute("orders", allOrders);
        return "user/my-orders";
    }

    @GetMapping("/comment")
    public String writeComment(Model model){
        model.addAttribute("products", userProductDAO.getAllProducts());
        return "user/show-products-to-comment";
    }
    @PostMapping("/comment/{id}")
    public String writeCommenttoProduct(@PathVariable("id")Long id, Model model){
        model.addAttribute("product", userProductDAO.getProductById(id));
        return "user/comment-to-product";
    }

    @PostMapping("/comment-to-product/{id}")
    public String commentToProduct(@PathVariable("id")Long id,
                                   @RequestParam("rating") int rating,
                                    @RequestParam("reason") String reason,
                                    HttpSession session){
        Product product = userProductDAO.getProductById(id);
        User currentUser = (User)session.getAttribute("currentUser");

        Complaint complaint = new Complaint();
        complaint.setRatingProduct(product);
        complaint.setRating(rating);
        complaint.setReason(reason);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setFiledBy(currentUser);
        if (rating >= 3){
            complaint.setStatus(CommentStatus.APPROVED);
        }else {
            complaint.setStatus(CommentStatus.PENDING);
        }
        complaintServiceDAO.saveComplaint(complaint);


        return "redirect:/user-cabinet";
    }

//    @PostMapping("/cancel-product/{id}")
//    public String cancelProduct(@PathVariable("id")Long id, HttpSession session, Model model){
//
//    }


    @GetMapping("/history")
    public String userHistory(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("currentUser");
        User updatedUser = userDAO.getUserById(currentUser.getId());
        model.addAttribute("histories", userProductDAO.getUserOrderHistory(updatedUser.getId()));
        return "user/user-history";
    }
}
