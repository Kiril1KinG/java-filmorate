package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genre;", genreRowMapper());
    }

    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM genre WHERE genre_id = ?",
                    genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Genre not found: Incorrect id");
        }
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
