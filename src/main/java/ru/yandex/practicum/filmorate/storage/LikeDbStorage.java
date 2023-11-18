package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addLike(int filmId, int userId) {
        List<Integer> like = jdbcTemplate.query("SELECT * FROM \"like\" WHERE film_id = ? AND user_id = ?",
                (rs, rowNum) -> rs.getInt("film_id"),
                filmId, userId);
        if (like.size() != 0) {
            throw new RuntimeException("Add like failed: like already exists");
        }
        jdbcTemplate.update("INSERT INTO \"like\" (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM \"like\" WHERE film_id = ? AND user_id = ?", filmId, userId);
    }
}
