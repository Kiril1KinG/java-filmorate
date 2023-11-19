package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        if (containsFilmById(film.getId())) {
            throw new DataNotFoundException("Add film failed: film already exists");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement("INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        List<Integer> filmId = jdbcTemplate.query("SELECT * FROM film WHERE film_id = ?",
                ((rs, rowNum) -> rs.getInt("film_id")), film.getId());
        if (filmId.size() != 1) {
            throw new DataNotFoundException("Update film failed: film not found");
        }
        jdbcTemplate.update("UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?," +
                        " rating_id = ? WHERE film_id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String query = "SELECT f.film_id film_id, f.name name, f.description description," +
                "f.release_date release_date, f.duration duration, f.rating_id rating_id, r.name rating_name " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "ORDER BY film_id;";
        List<Film> films = jdbcTemplate.query(query, filmRowMapper());
        enrichFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String query = "SELECT f.film_id film_id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, f.rating_id rating_id, r.name rating_name " +
                "FROM film AS f " +
                "JOIN rating AS r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        List<Film> film = jdbcTemplate.query(query, filmRowMapper(), id);
        if (film.size() != 1) {
            throw new DataNotFoundException("Film not found: Incorrect id");
        }
        enrichFilms(film);
        return film.get(0);
    }

    @Override
    public boolean containsFilmById(int id) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Long.class, id);
        return count == 1;
    }

    private RowMapper<Film> filmRowMapper() {
        return ((rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"))));
    }

    private void enrichFilms(Collection<Film> films) {
        String genresForFilm = "SELECT * FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        String likesForFilm = "SELECT * FROM \"like\" WHERE film_id = ?";
        for (Film film : films) {
            Collection<Genre> genres = jdbcTemplate.query(genresForFilm,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")
                    ), film.getId());
            Collection<Integer> likes = jdbcTemplate.query(likesForFilm, ((rs, rowNum) -> rs.getInt("user_id")), film.getId());
            film.setGenres(new HashSet<>(genres));
            film.setLikes(new HashSet<>(likes));
        }
    }

    public void validateFilm(Film film, String operation) {
        List<Integer> ratingId = jdbcTemplate.query("SELECT rating_id FROM rating WHERE rating_id = ?",
                ((rs, rowNum) -> rs.getInt("rating_id")),
                film.getMpa().getId());
        if (ratingId.size() != 1) {
            throw new ValidationException(operation + " film failed: Incorrect rating");
        }
        List<Integer> genresId = jdbcTemplate.query("SELECT * FROM genre;",
                (rs, rowNum) -> rs.getInt("genre_id"));
        for (Genre genre : film.getGenres()) {
            if (!genresId.contains(genre.getId())) {
                throw new DataNotFoundException(operation + " film failed: Incorrect genres");
            }
        }
    }

    private void updateFilmGenres(Film film) {
        List<Integer> id = jdbcTemplate.query("SELECT * FROM film WHERE name = ? AND release_date = ?",
                (rs, rowNum) -> rs.getInt("film_id"), film.getName(), film.getReleaseDate());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", id.get(0));
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", id.get(0), genre.getId());
        }
    }


}

