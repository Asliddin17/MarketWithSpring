package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.OrderHistoryDTO;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.Shop;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.entity.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public List<Product> getAllProducts() {
        return jdbcTemplate.query(
                "SELECT p.*, s.name as shop_name FROM product p JOIN shop s ON p.shop_id = s.id where product_status = ?", new Object[]{ProductStatus.NEW.name()},
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(Long.valueOf(rs.getString("id")));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));

                    String status = rs.getString("product_status");
                    if (status != null) {
                        product.setProductStatus(ProductStatus.valueOf(status.toUpperCase()));
                    }

                    Shop shop = new Shop();
                    shop.setId(Long.valueOf(rs.getString("shop_id")));
                    shop.setName(rs.getString("shop_name"));
                    product.setShop(shop);

                    return product;
                }
        );
    }



    public Product getProductById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT p.*, s.name as shop_name FROM product p JOIN shop s ON p.shop_id = s.id WHERE p.id = ? and product_status = ?",
                new Object[]{id, ProductStatus.NEW.name()},
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(Long.valueOf(rs.getString("id")));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));
                    product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status").toUpperCase()));

                    Shop shop = new Shop();
                    shop.setId(Long.valueOf(rs.getString("shop_id")));
                    shop.setName(rs.getString("shop_name"));
                    product.setShop(shop);

                    return product;
                }
        );
    }

    public void buyProductById(Long id) {
        jdbcTemplate.update("update table product set count = count - 1 where id = ? and  = ?", id, OrderStatus.CONFIRMED);
    }

    public void decreaseProductCount(Long id) {
        jdbcTemplate.update("update product set count = count - 1 where id = ?", id);
    }

    public List<Product> getAllProductsWithConfirmedStatus(Long userId) {
        return jdbcTemplate.query(
                "SELECT p.*, s.name AS shop_name, up.id AS order_id " +
                        "FROM order_table up " +
                        "JOIN product p ON up.product_id = p.id " +
                        "JOIN shop s ON p.shop_id = s.id " +
                        "WHERE up.user_id = ? AND up.status = ?",
                new Object[]{userId, OrderStatus.CONFIRMED.name()},
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setOrderId(rs.getLong("order_id"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));
                    product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status").toUpperCase()));

                    Shop shop = new Shop();
                    shop.setId(rs.getLong("shop_id"));
                    shop.setName(rs.getString("shop_name"));
                    product.setShop(shop);

                    return product;
                }
        );
    }


//    public void cancelProduct(Long id) {
//        jdbcTemplate.update("UPDATE product SET product_status = ? WHERE id = ?", ProductStatus.CANCELLED.name(), id);
//    }

    public void increaseProductCount(Long id) {
        jdbcTemplate.update("update product set count = count + 1 where id = ?", id);
    }

    public List<OrderHistoryDTO> getUserOrderHistory(Long userId) {
        String sql = "SELECT p.*, o.status as order_status, sh.name as shop_name, o.created_at as creation_time " +
                "FROM order_table o " +
                "JOIN product p ON p.id = o.product_id " +
                "JOIN shop sh ON p.shop_id = sh.id " +
                "WHERE o.user_id = ? AND o.status IN (?, ?)";
        return jdbcTemplate.query(sql,new Object[]{userId, OrderStatus.CONFIRMED.name(), OrderStatus.CANCELLED.name()},
                (rs, rowNum) ->{
                    OrderHistoryDTO dto = new OrderHistoryDTO();
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));
                    product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status").toUpperCase()));
                    Shop shop = new Shop();
                    shop.setName(rs.getString("shop_name"));
                    product.setShop(shop);
                    dto.setProduct(product);
                    dto.setOrderStatus(OrderStatus.valueOf(rs.getString("order_status").toUpperCase()));
                    dto.setDateTime(rs.getTimestamp("creation_time").toLocalDateTime());
                    return dto;
                });
    }



    public void cancelOrder(Long orderId) {
        jdbcTemplate.update("update order_table set status = ?" +
                " where id= ?  and status = ?",
                OrderStatus.CANCELLED.name(), orderId, OrderStatus.CONFIRMED.name()
                );
    }

    public Product getUserOrderProduct(Long userId, Long productId) {
        List<Product> result = jdbcTemplate.query(
                "SELECT p.*, s.name AS shop_name FROM user_product up " +
                        "JOIN product p ON up.product_id = p.id " +
                        "JOIN shop s ON p.shop_id = s.id " +
                        "WHERE up.user_id = ? AND up.product_id = ? AND up.order_status = ?",
                new Object[]{userId, productId, OrderStatus.CONFIRMED.name()},
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));
                    product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status")));

                    Shop shop = new Shop();
                    shop.setId(rs.getLong("shop_id"));
                    shop.setName(rs.getString("shop_name"));
                    product.setShop(shop);
                    return product;
                }
        );
        return result.stream().findFirst().orElse(null);
    }



    public List<Product> getUserProducts(Long userId) {
        String sql = """
        SELECT p.*, s.name AS shop_name
        FROM product p
        JOIN shop s ON p.shop_id = s.id
        JOIN user_product up ON p.id = up.product_id
        WHERE up.user_id = ? AND up.order_status = ?
    """;

        return jdbcTemplate.query(sql, new Object[]{userId, OrderStatus.CONFIRMED.name()}, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            product.setRatings(rs.getInt("ratings"));
            product.setCount(rs.getInt("count"));
            product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status")));

            Shop shop = new Shop();
            shop.setId(rs.getLong("shop_id"));
            shop.setName(rs.getString("shop_name"));
            product.setShop(shop);

            return product;
        });
    }

    public Product getProductByOrderId(Long orderId) {
        String sql = """
        SELECT p.*, s.name AS shop_name
        FROM order_table o
        JOIN product p ON o.product_id = p.id
        JOIN shop s ON p.shop_id = s.id
        WHERE o.id = ?
    """;

        return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            product.setRatings(rs.getInt("ratings"));
            product.setCount(rs.getInt("count"));
            product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status").toUpperCase()));

            Shop shop = new Shop();
            shop.setId(rs.getLong("shop_id"));
            shop.setName(rs.getString("shop_name"));
            product.setShop(shop);

            return product;
        });
    }



//    public void addToUserProductTable(Long userId, Long productId) {
//        jdbcTemplate.update("""
//        INSERT INTO user_product (user_id, product_id, order_status)
//        VALUES (?, ?, ?)
//        ON CONFLICT (user_id, product_id)
//        DO UPDATE SET order_status = EXCLUDED.order_status
//    """, userId, productId, OrderStatus.CONFIRMED.name());
//    }
}
