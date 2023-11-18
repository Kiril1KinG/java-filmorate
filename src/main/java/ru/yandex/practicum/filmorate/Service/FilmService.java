package ru.yandex.practicum.filmorate.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {

    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;


    //TODO likeDAO????
    public Film addLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new DataNotFoundException("Add like failed: Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Add like failed: Incorrect user id");
        }
        likeDbStorage.addLike(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Like added: {}", film);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new DataNotFoundException("Delete like failed: Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete like failed: Incorrect user id");
        }
        likeDbStorage.deleteLike(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Like deleted: {}", film);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
        log.info("Top films received: {}", sortedFilms);
        return sortedFilms;
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        log.info("Film added: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilmById(film.getId())) {
            throw new DataNotFoundException("Update film failed: Film not found");
        }
        log.info("Film updated: {}", film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        log.info("Films received: {}", films);
        return films;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM)) {
            throw new ValidationException("Validation failed: Incorrect release date");
        }
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        log.info("Film by id received: {}", film);
        return film;
    }

}
