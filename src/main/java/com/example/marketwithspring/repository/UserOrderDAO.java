package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserOrderDAO {
    private final JdbcTemplate jdbcTemplate;

    public Long addProductToOrderList(Product product, User user, String historyId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into order_table(user_id, product_id, quantity, status, created_at) values(?,?,?,?,?)",
                    new String[]{"id"}

            );
            ps.setLong(1, user.getId());
            ps.setLong(2, product.getId());
            ps.setInt(3, 1);
            ps.setString(4, OrderStatus.PROCESSING.name());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }


    public Order getOrderById(Long orderId) {
        return jdbcTemplate.queryForObject(
                "select * from order_table where id = ?", new Object[]{orderId},
                (rs, rowNum) -> {
                    Order order = new Order();
                    order.setId(Long.valueOf(rs.getString("id")));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));

                    order.setQuantity(rs.getInt("quantity"));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    Product product = new Product();
                    product.setId(rs.getLong("product_id"));
                    order.setUser(user);
                    order.setProduct(product);
                    return order;
                });
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        jdbcTemplate.update("uptade orders table set status = ? where id = ?", orderStatus, orderId);
    }

    public List<Order> getAllOrders() {
        return jdbcTemplate.query("select * from order_table where status in (?, ?)", new Object[]{OrderStatus.PROCESSING.name(), OrderStatus.CONFIRMED.name()},
                (rs, rowNum) -> {
                    Order order = new Order();
                    order.setId(Long.valueOf(rs.getString("id")));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));

                    order.setQuantity(rs.getInt("quantity"));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    Product product = new Product();
                    product.setId(rs.getLong("product_id"));
                    order.setUser(user);
                    order.setProduct(product);
                    return order;
                });
    }
}
