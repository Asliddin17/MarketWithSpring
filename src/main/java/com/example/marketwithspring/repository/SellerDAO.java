package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SellerDAO {
    private final JdbcTemplate jdbcTemplate;

    public void createShop(String name) {
        jdbcTemplate.update("insert into shop (name) values (?)", name);
    }

    public List<Shop> getAllShops() {
        return jdbcTemplate.query("select * from shop ORDER BY id DESC", BeanPropertyRowMapper.newInstance(Shop.class));
    }

    public Shop getShopById(Long id) {
        return jdbcTemplate.queryForObject("select * from shop where id=?", BeanPropertyRowMapper.newInstance(Shop.class), id);
    }

    public void updateShop(Long id, String name) {
        jdbcTemplate.update("update shop set name=? where id=?", name, id);
    }

    public void deleteShop(Long id) {
        jdbcTemplate.update("delete from shop where id=?", id);
    }

    public List<Product> getProductsByShopId(Long shopId) {
        return jdbcTemplate.query("select * from product where shop_id=?", BeanPropertyRowMapper.newInstance(Product.class), shopId);
    }

}