package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM rating", ratingRowMapper());
    }

    public Mpa getRatingById(int id) {
        List<Mpa> mpa = jdbcTemplate.query("SELECT * FROM rating WHERE rating_id = ?", ratingRowMapper(), id);
        if (mpa.size() != 1) {
            throw new DataNotFoundException("Rating not found: Incorrect id");
        }
        return mpa.get(0);
    }

    private RowMapper<Mpa> ratingRowMapper() {
        return ((rs, rowNum) -> new Mpa(
                rs.getInt("rating_id"),
                rs.getString("name")
        ));
    }
}
