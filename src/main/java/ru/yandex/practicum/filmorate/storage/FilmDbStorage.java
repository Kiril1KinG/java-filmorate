package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement("INSERT INTO film (name, description, release_date, duration, rating_id)" +
                                    " VALUES (?, ?, ?, ?, ?)",
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
        jdbcTemplate.update("UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?," +
                        " rating_id = ? WHERE film_id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String query = "SELECT *, r.name rating_name " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "ORDER BY film_id;";
        List<Film> films = jdbcTemplate.query(query, this::mapFilm);
        enrichFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String query = "SELECT *, r.name rating_name " +
                "FROM film AS f " +
                "JOIN rating AS r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(query, this::mapFilm, id);
            enrichFilms(List.of(film));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Get film failed: Incorrect id");
        }
    }

    @Override
    public boolean containsFilmById(int id) {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Long.class, id);
            return count == 1;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Film mapFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")));
        return film;
    }

    private void enrichFilms(Collection<Film> films) {
        String genresForFilm = "SELECT * FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        String likesForFilm = "SELECT * FROM \"like\" WHERE film_id = ?";
        String directorsForFilm = "SELECT * FROM director_film AS df " +
                "JOIN directors AS d ON df.director_id = d.id " +
                "WHERE film_id = ?";

        for (Film film : films) {
            Collection<Genre> genres = jdbcTemplate.query(genresForFilm,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")
                    ), film.getId());
            Collection<Integer> likes = jdbcTemplate.query(likesForFilm,
                    ((rs, rowNum) -> rs.getInt("user_id")), film.getId());
            Collection<Director> directors = jdbcTemplate.query(directorsForFilm,
                    (rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("name")
                    ), film.getId());
            film.setGenres(new HashSet<>(genres));
            film.setLikes(new HashSet<>(likes));
            film.setDirectors(new HashSet<>(directors));
        }
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", film.getId(), genre.getId());
        }
    }

    public void addLike(int filmId, int userId) {
        Integer like = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"like\" WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId);
        if (like != 0) {
            throw new RuntimeException("Add like failed: like already exists");
        }
        jdbcTemplate.update("INSERT INTO \"like\" (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM \"like\" WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    public void validateFilm(Film film, String operation) {
        try {
            jdbcTemplate.queryForObject("SELECT rating_id FROM rating WHERE rating_id = ?", Integer.class,
                    film.getMpa().getId());
            List<Integer> genresId = jdbcTemplate.query("SELECT * FROM genre;",
                    (rs, rowNum) -> rs.getInt("genre_id"));
            for (Genre genre : film.getGenres()) {
                if (!genresId.contains(genre.getId())) {
                    throw new DataNotFoundException(operation + " film failed: Incorrect genres");
                }
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException(operation + " film failed: Incorrect rating");
        }
    }

    @Override
    public List<Film> getSortedFilmsByDirector(int directorId, List<String> sortBy) {
        String query = "";
        if (sortBy.contains("year")) {
            query = "SELECT f.*, r.name AS rating_name FROM film AS f " +
                    "JOIN rating AS r ON f.rating_id = r.rating_id " +
                    "JOIN director_film AS df ON f.film_id = df.film_id " +
                    "WHERE df.director_id = ? ORDER BY f.release_date";
        }
        if (sortBy.contains("likes")) {
            query = "SELECT f.*, r.name AS rating_name FROM film AS f " +
                    "JOIN rating AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN \"like\" AS l ON f.film_id=l.film_id " +
                    "JOIN director_film AS df ON f.film_id=df.film_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC";
        }
        List<Film> films = jdbcTemplate.query(query, this::mapFilm, directorId);
        enrichFilms(films);
        return films;
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder("SELECT f.*, r.name AS rating_name FROM film AS f " +
                "JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN \"like\" AS l ON f.film_id=l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id ");
        int index = sql.length();
        sql.append("GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?");
        if (genreId != null && year != null) {
            sql.insert(index, "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? ");
            return query(sql.toString(), genreId, year, count);
        }
        if (genreId != null) {
            sql.insert(index, "WHERE fg.genre_id = ? ");
            return query(sql.toString(), genreId, count);
        }
        if (year != null) {
            sql.insert(index, "WHERE EXTRACT(YEAR FROM f.release_date) = ? ");
            return query(sql.toString(), year, count);
        }
        return query(sql.toString(), count);
    }

    private List<Film> query(String sql, Integer... args) {
        List<Film> films = jdbcTemplate.query(sql, this::mapFilm, (Object[]) args);
        enrichFilms(films);
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String query = "SELECT f.FILM_ID ,f.name, f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , f.RATING_ID, r.name rating_name " +
                "FROM film f " +
                "JOIN \"like\" l1 ON f.film_id = l1.film_id " +
                "JOIN \"like\" l2 ON f.film_id = l2.film_id " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "WHERE l1.user_id = ? AND l2.user_id = ?";
        List<Film> films = jdbcTemplate.query(query, this::mapFilm, userId, friendId);
        enrichFilms(films);
        return films;
    }

    @Override
    public void deleteFilm(int filmId) {
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", filmId);
    }


    @Override
    public List<Film> searchFilms(String query, String by) {
        String filmSearchQuery = "";
        String directorSearchQuery = "";
        List<Object> directorParameters = new ArrayList<>();
        List<Object> filmParameters = new ArrayList<>();
        List<Film> filmNameResult = new ArrayList<>();
        List<Film> directorResult = new ArrayList<>();

        if (by.contains("director")) {
            directorSearchQuery = "SELECT DISTINCT f.*, r.name as rating_name, g.name as genre_name, d.name as director_name, " +
                    "fg.genre_id, df.director_id " +
                    "FROM film f " +
                    "JOIN director_film df ON f.film_id = df.film_id " +
                    "JOIN directors d ON df.director_id = d.id " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "WHERE LOWER(d.name) LIKE LOWER(?)";
            directorParameters.add("%" + query + "%");
            directorResult = jdbcTemplate.query(directorSearchQuery, this::mapFilm, directorParameters.toArray());
            enrichFilms(directorResult);
        }
        if (by.contains("title")) {
            filmSearchQuery = "SELECT DISTINCT f.*, r.name as rating_name, g.name as genre_name, d.name as director_name, " +
                    "fg.genre_id, df.director_id " +
                    "FROM film f " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                    "LEFT JOIN directors d ON df.director_id = d.id " +
                    "WHERE LOWER(f.name) LIKE LOWER(?)";
            filmParameters.add("%" + query + "%");
            filmNameResult = jdbcTemplate.query(filmSearchQuery, this::mapFilm, filmParameters.toArray());
            enrichFilms(filmNameResult);
        }
        List<Film> result = Stream.concat(directorResult.stream(), filmNameResult.stream()).collect(Collectors.toList());

        if (result.isEmpty()) {
            log.info("No films found for the query: {} by {}", query, by);
        }

        return result;
    }

}

