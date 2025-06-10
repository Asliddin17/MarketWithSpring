package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Order;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDAO {

    private final JdbcTemplate jdbcTemplate;

    public List<Order> getAllOrders() {
        String sql = "SELECT o.id AS order_id, u.id AS user_id, u.name AS user_name, p.id AS product_id, p.name AS product_name, " +
                "o.quantity, o.status, o.created_at " +
                "FROM order_table o " + // Corrected table name
                "LEFT JOIN users u ON o.user_id = u.id " + // Corrected to match schema
                "LEFT JOIN product p ON o.product_id = p.id " + // Corrected to match schema
                "ORDER BY o.id DESC";
        return jdbcTemplate.query(sql, new OrderRowMapperWithDetails());
    }

    public Order getOrderById(Long id) {
        String sql = "SELECT o.id AS order_id, u.id AS user_id, u.name AS user_name, p.id AS product_id, p.name AS product_name, " +
                "o.quantity, o.status, o.created_at " +
                "FROM order_table o " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN product p ON o.product_id = p.id " +
                "WHERE o.id = ?";
        return jdbcTemplate.queryForObject(sql, new OrderRowMapperWithDetails(), id);
    }

    public void updateOrder(Order order) {
        String sql = "UPDATE order_table SET user_id = ?, product_id = ?, quantity = ?, status = ?, created_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                order.getUser() != null ? order.getUser().getId() : null,
                order.getProduct() != null ? order.getProduct().getId() : null,
                order.getQuantity(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getId());
    }

    private static class OrderRowMapperWithDetails implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("order_id"));

            Long userId = rs.getLong("user_id");
            if (!rs.wasNull()) {
                User user = new User();
                user.setId(userId);
                user.setName(rs.getString("user_name"));
                order.setUser(user);
            }

            Long productId = rs.getLong("product_id");
            if (!rs.wasNull()) {
                Product product = new Product();
                product.setId(productId);
                product.setName(rs.getString("product_name"));
                order.setProduct(product);
            }

            order.setQuantity(rs.getInt("quantity"));
            order.setStatus(OrderStatus.valueOf(rs.getString("status")));
            order.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

            return order;
        }
    }
}