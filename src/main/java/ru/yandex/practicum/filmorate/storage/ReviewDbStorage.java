package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement("INSERT INTO review (content, is_positive, useful, user_id, film_id)" +
                                    " VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, 0);
            ps.setInt(4, review.getUserId());
            ps.setInt(5, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        return review;
    }

    public Review getReviewById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM review WHERE review_id = ?", this::mapReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Review not found: Incorrect id");
        }
    }

    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        if (filmId == 0) {
            return jdbcTemplate.query("SELECT * FROM review ORDER BY useful DESC," +
                    " review_id ASC LIMIT ?", this::mapReview, count);
        }
        return jdbcTemplate.query("SELECT * FROM review WHERE film_id = ? ORDER BY useful DESC," +
                " review_id ASC LIMIT ?", this::mapReview, filmId, count);
    }

    public Review updateReview(Review review) {
        jdbcTemplate.update("UPDATE review SET content = ?, is_positive = ? WHERE review_id = ?",
                review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    public void deleteReviewById(int id) {
        jdbcTemplate.update("DELETE FROM review WHERE review_id = ?", id);
    }

    public boolean containsReviewById(int id) {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review WHERE review_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Review mapReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUseful(rs.getInt("useful"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        return review;
    }

    public void addLike(int id, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = ? WHERE review_id = ?", getUsefulById(id) + 1, id);
        jdbcTemplate.update("INSERT INTO review_like (review_id, user_id, is_positive) VALUES (?, ?, ?)",
                id, userId, true);
    }

    public void addDislike(int id, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = ? WHERE review_id = ?", getUsefulById(id) - 1, id);
        jdbcTemplate.update("INSERT INTO review_like (review_id, user_id, is_positive) VALUES (?, ?, ?)",
                id, userId, false);
    }

    public void deleteLike(int id, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = ? WHERE review_id = ?", getUsefulById(id) - 1);
        jdbcTemplate.update("DELETE FROM review_like WHERE review_id = ? AND user_id = ?", id, userId);
    }

    public void deleteDislike(int id, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = ? WHERE review_id = ?", getUsefulById(id) + 1);
        jdbcTemplate.update("DELETE FROM review_like WHERE review_id = ? AND userId = ?", id, userId);
    }

    private int getUsefulById(int id) {
        return jdbcTemplate.queryForObject("SELECT useful FROM review WHERE review_id = ?", Integer.class, id);
    }

    public boolean isReviewContainsLikeOrDislikeFromUser(int id, int userId, boolean isPositive) {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review_like WHERE review_id = ? AND" +
                    " user_id = ? AND is_positive = ?", Long.class, id, userId, isPositive);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
