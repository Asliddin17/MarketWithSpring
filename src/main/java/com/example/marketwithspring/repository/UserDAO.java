package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.UserRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject(
                "select * from users where id = ?", new Object[]{id},
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(Long.valueOf(rs.getString("id")));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setBalance(rs.getDouble("balance"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(UserRole.valueOf(rs.getString("role")));
                    return user;
                });
    }

    public void deductUserBalance(Long id, double price) {
        jdbcTemplate.update("update users set balance = balance - ? where id = ?", price, id);
    }

    public User getUserByEmailAndPassword(String email, String password) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from users where email = ? and password = ?", new BeanPropertyRowMapper<>(User.class),
                    email, password
            );

        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void refundBalanceToUser(User user, Double price) {
        price = price * 0.8;
        jdbcTemplate.update("update users set balance = balance + ? where id = ?", price, user.getId());
    }

    public void saveUser(User user) {
        jdbcTemplate.update("insert into users(name, email, password, balance, role) values (?,?,?,?,?)",
                user.getName(), user.getEmail(), user.getPassword(), user.getBalance(), UserRole.USER.name());
    }
}
