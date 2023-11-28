package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genre;", this::mapGenre);
    }

    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genre WHERE genre_id = ?",
                    this::mapGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Genre not found: Incorrect id");
        }
    }

    private Genre mapGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
