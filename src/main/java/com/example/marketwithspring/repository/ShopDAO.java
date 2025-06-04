package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ShopDAO{
    private final JdbcTemplate jdbcTemplate;


    public List<Shop> getAllShops(){
       return jdbcTemplate.query("select * from shops", BeanPropertyRowMapper.newInstance(Shop.class));
    }
}
