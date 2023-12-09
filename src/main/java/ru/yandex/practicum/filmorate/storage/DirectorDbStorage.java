package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectors() {
        String query = "SELECT * FROM directors";
        return jdbcTemplate.query(query, this::mapDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        String query = "SELECT * FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(query, this::mapDirector, id);
        if (directors.size() != 1) {
            throw new DataNotFoundException(String.format("Director with id=%s not single", id));
        }
        return directors.get(0);
    }

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        int id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String query = "UPDATE directors SET " +
                "name = ? WHERE id = ?";
        jdbcTemplate.update(query, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String query = "DELETE FROM director_film WHERE director_id = ?";
        jdbcTemplate.update(query, id);
        query = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public boolean containsDirectorById(int id) {
        try {
            getDirectorById(id);
            return true;
        } catch (DataNotFoundException e) {
            return false;
        }
    }

    @Override
    public Set<Director> getFilmDirectors(int filmId) {
        String query = "SELECT d.* from director_film AS df join directors AS d ON d.id=df.director_id "
                + "WHERE df.film_id = ?";
        List<Director> directors = jdbcTemplate.query(query, this::mapDirector, filmId);
        return new HashSet<>(directors);
    }

    @Override
    public void addFilmDirectors(Film film) {
        String query = "INSERT INTO director_film(film_id, director_id) " +
                "VALUES (?, ?)";
        film.getDirectors().forEach(director -> jdbcTemplate.update(query, film.getId(), director.getId()));
    }

    @Override
    public void deleteFilmDirectors(Film film) {
        String query = "DELETE FROM director_film WHERE film_id = ?";
        jdbcTemplate.update(query, film.getId());
    }


    private Director mapDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();

    }
}
