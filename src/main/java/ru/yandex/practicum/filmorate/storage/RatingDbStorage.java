package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM rating", this::mapMpa);
    }

    public Mpa getRatingById(int id) {
        List<Mpa> mpa = jdbcTemplate.query("SELECT * FROM rating WHERE rating_id = ?", this::mapMpa, id);
        if (mpa.size() != 1) {
            throw new DataNotFoundException("Rating not found: Incorrect id");
        }
        return mpa.get(0);
    }

    private Mpa mapMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;

    }
}
