package com.example.marketwithspring.service;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.repository.UserDAO;
import com.example.marketwithspring.repository.UserHistoryDAO;
import com.example.marketwithspring.repository.UserOrderDAO;
import com.example.marketwithspring.repository.UserProductDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserOrderDAO userOrderDAO;
    private final UserProductDAO userProductDAO;
    private final UserHistoryDAO userHistoryDAO;
    private final UserDAO userDAO;

    public void confirmOrder(Long orderId){
        Order order = userOrderDAO.getOrderById(orderId);
        Product product = userProductDAO.getProductById(order.getProduct().getId());
        User user = userDAO.getUserById(order.getUser().getId());

        if(product.getCount()>0 && user.getBalance().compareTo(product.getPrice()) >= 0){
            userOrderDAO.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
            userDAO.deductUserBalance(user.getId(), product.getPrice());
            userProductDAO.decreaseProductCount(product.getId());
        }else {
            throw new RuntimeException("Yetarli balans yoki mahsulot mavjud emas");
        }

    }
}
