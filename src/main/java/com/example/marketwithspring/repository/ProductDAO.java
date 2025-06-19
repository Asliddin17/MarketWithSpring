package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.Shop;
import com.example.marketwithspring.entity.enums.CommentStatus;
import com.example.marketwithspring.entity.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDAO {
    private final JdbcTemplate jdbcTemplate;

    public void createProduct(String name, double price, Shop shop, Integer ratings, Integer count, String productStatus) {
        Long shopId = shop != null ? shop.getId() : null;
        jdbcTemplate.update(
                "INSERT INTO product (name, price, shop_id, ratings, count, product_status) VALUES (?, ?, ?, ?, ?, ?)",
                name, price, shopId, ratings, count, productStatus
        );
    }

//    public List<Product> getAllProducts() {
//        return jdbcTemplate.query(
//                "SELECT p.id AS id, p.name AS name, p.price AS price, p.shop_id AS shop_id, p.ratings AS ratings, p.count AS count, p.product_status AS product_status FROM product p ORDER BY p.id DESC",
//                new ProductRowMapper()
//        );
//    }

    public List<Product> getAllProducts() {
        return jdbcTemplate.query(
                "SELECT p.id AS id, p.name AS name, p.price AS price, p.shop_id AS shop_id, " +
                        "COALESCE(AVG(c.rating), 0) AS ratings, " +  // approved ratinglar bo'yicha o'rtacha
                        "p.count AS count, p.product_status AS product_status " +
                        "FROM product p " +
                        "LEFT JOIN complaint c ON c.target_product = p.id AND c.status = ? " +
                        "GROUP BY p.id, p.name, p.price, p.shop_id, p.count, p.product_status " +
                        "ORDER BY p.id DESC",
                new ProductRowMapper(),
                CommentStatus.APPROVED.name()
        );
    }
//    public double getAvgRatingForProduct(Long id){
//        String sql = "select avg(rating) from complaint where target_product = ? and status = ?";
//        Double avg = jdbcTemplate.queryForObject(sql, new Object[]{id, CommentStatus.APPROVED.name()}, Double.class);
//        return avg!=null ? avg : 0.0;
//    }


    public Product getProductById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT p.id AS id, p.name AS name, p.price AS price, p.shop_id AS shop_id, p.ratings AS ratings, p.count AS count, p.product_status AS product_status FROM product p WHERE p.id = ?",
                new ProductRowMapper(), id
        );
    }

    public void updateProduct(Long id, String name, double price, Shop shop, Integer ratings, Integer count, String productStatus) {
        Long shopId = shop != null ? shop.getId() : null;
        jdbcTemplate.update(
                "UPDATE product SET name = ?, price = ?, shop_id = ?, ratings = ?, count = ?, product_status = ? WHERE id = ?",
                name, price, shopId, ratings, count, productStatus, id
        );
    }

    public void deleteProduct(Long id) {
        jdbcTemplate.update("DELETE FROM product WHERE id = ?", id);
    }

    public List<Product> getProductsByShopId(Long shopId) {
        return jdbcTemplate.query(
                "SELECT p.id AS id, p.name AS name, p.price AS price, p.shop_id AS shop_id, p.ratings AS ratings, p.count AS count, p.product_status AS product_status FROM product p WHERE p.shop_id = ?",
                new ProductRowMapper(), shopId
        );
    }

    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));

            long shopId = rs.getLong("shop_id");
            if (!rs.wasNull()) {
                Shop shop = new Shop();
                shop.setId(shopId);
                product.setShop(shop);
            }

            Double avgRating = rs.getObject("ratings") != null ? rs.getDouble("ratings") : null;
            product.setRatings(avgRating != null ? avgRating.intValue() : null);

            product.setCount(rs.getObject("count") != null ? rs.getInt("count") : null);
            product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status")));

            return product;
        }
    }


//    private static class ProductRowMapper implements RowMapper<Product> {
//        @Override
//        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Product product = new Product();
//            product.setId(rs.getLong("id"));
//            product.setName(rs.getString("name"));
//            product.setPrice(rs.getDouble("price"));
//            long shopId = rs.getLong("shop_id");
//            if (!rs.wasNull()) { // Check if shop_id is not null
//                Shop shop = new Shop();
//                shop.setId(shopId);
//                product.setShop(shop);
//            }
//            product.setRatings(rs.getObject("ratings") != null ? rs.getInt("ratings") : null);
//            product.setCount(rs.getObject("count") != null ? rs.getInt("count") : null);
//            product.setProductStatus(ProductStatus.valueOf(rs.getString("product_status")));
//            return product;
//        }
//    }

    public List<Product> getAllProductsWithApprovedStatus() {
        return jdbcTemplate.query(
                "SELECT p.id AS id, p.name AS name, p.price AS price, p.shop_id AS shop_id, p.ratings AS ratings, p.count AS count, p.product_status AS product_status " +
                        "FROM product p " +
                        "JOIN complaint c ON c.target_product = p.id " + // ← BU YERGA SPACE QO‘SHILDI
                        "WHERE c.status = ? " +
                        "ORDER BY p.id DESC",
                new ProductRowMapper(),
                CommentStatus.APPROVED.name()
        );
    }
}