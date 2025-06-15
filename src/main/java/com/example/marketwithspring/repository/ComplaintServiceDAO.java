package com.example.marketwithspring.repository;

import com.example.marketwithspring.entity.Complaint;
import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.User;
import com.example.marketwithspring.entity.enums.CommentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ComplaintServiceDAO {
    private final JdbcTemplate jdbcTemplate;

    public void saveComplaint(Complaint complaint) {
        String sql = "insert into complaint (reason, rating, filed_by, target_product, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                complaint.getReason(),
                complaint.getRating(),
                complaint.getFiledBy().getId(),
                complaint.getRatingProduct().getId(),
                complaint.getStatus().name(),
                complaint.getCreatedAt());
    }

    public List<Complaint> getAllCommentsRelatedToProduct(Long id) {
        return jdbcTemplate.query("select c.*, u.name from complaint c join users u on c.filed_by = u.id where  target_product = ? and status = ?", new Object[]{id, CommentStatus.APPROVED.name()},
                (rs, rowNum) -> {
                    Complaint complaint = new Complaint();
                    complaint.setId(rs.getLong("id"));
                    complaint.setReason(rs.getString("reason"));
                    User user = new User();
                    user.setId(rs.getLong("filed_by"));
                    user.setName(rs.getString("name"));
                    Product product = new Product();
                    product.setId(rs.getLong("target_product"));
                    complaint.setFiledBy(user);
                    complaint.setRatingProduct(product);
                    complaint.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    complaint.setRating(rs.getInt("rating"));
                    complaint.setStatus(CommentStatus.valueOf(rs.getString("status")));
                    return complaint;
                });
    }

    public double getAvgRatingForProduct(Long id){
        String sql = "select avg(rating) from complaint where target_product = ? and status = ?";
        Double avg = jdbcTemplate.queryForObject(sql, new Object[]{id, CommentStatus.APPROVED.name()}, Double.class);
        return avg!=null ? avg : 0.0;
    }
}
