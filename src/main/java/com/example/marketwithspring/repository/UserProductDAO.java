package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.Shop;
import com.example.marketwithspring.entity.enums.OrderStatus;
import com.example.marketwithspring.entity.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserProductDAO {

    private final JdbcTemplate jdbcTemplate;

    public List<Product> getAllProducts() {
        return jdbcTemplate.query(
                "SELECT p.*, s.name as shop_name FROM product p JOIN shop s ON p.shop_id = s.id",
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
                "SELECT p.*, s.name as shop_name FROM product p JOIN shop s ON p.shop_id = s.id WHERE p.id = ?",
                new Object[]{id},
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
        jdbcTemplate.update("update product table set count = count - 1 where id = ?", id);
    }

    public List<Product> getAllProductsWithConfirmedStatus() {
        return jdbcTemplate.query("select p.*, o.status from product p join order_table o on p.id = o.product_id where o.status = ? ", new Object[]{OrderStatus.CONFIRMED.name()},
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(Long.valueOf(rs.getString("id")));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setRatings(rs.getInt("ratings"));
                    product.setCount(rs.getInt("count"));
                    product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status").toUpperCase()));

                    Shop shop = new Shop();
                    shop.setId(Long.valueOf(rs.getString("id")));
                    shop.setName(rs.getString("name"));
                    product.setShop(shop);

                    return product;
                }
        );
    }
}
