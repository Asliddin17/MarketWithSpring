package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserHistoryDAO {
    private final JdbcTemplate jdbcTemplate;

    public String getOrCreateHistoryIdByUser(User user) {
        String sql = "select id from history where user_id = ?";
        List<String> ids = jdbcTemplate.queryForList(sql, String.class, user.getId());

        if(!ids.isEmpty()){
            return ids.get(0);
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("insert into history(user_id) values (?)", new String[]{"id"});
            ps.setLong(1, user.getId());
            return ps;
        }, keyHolder);
        Long newId = keyHolder.getKey().longValue();
        return String.valueOf(newId);
    }

    public void addToHistory(User user, Product product, Long orderId) {
        // 1. user uchun history id olib kelamiz yoki yaratamiz
        String historyId = getOrCreateHistoryIdByUser(user);

        // 2. tarixga yozamiz
        jdbcTemplate.update(
                "insert into history_orders(history_id, order_id, product_id, bought_at) values(?,?,?,?)",
                Long.parseLong(historyId),
                orderId,
                product.getId(),
                LocalDateTime.now()
        );
    }

}
