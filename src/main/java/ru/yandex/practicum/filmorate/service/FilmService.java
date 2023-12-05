package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
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


    public void addLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new DataNotFoundException("Add like failed: Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Add like failed: Incorrect user id");
        }
        filmStorage.addLike(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Like added: {}", film);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new DataNotFoundException("Delete like failed: Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete like failed: Incorrect user id");
        }
        filmStorage.deleteLike(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Like deleted: {}", film);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
        sortedFilms.forEach(film -> film.setDirectors(directorStorage.getFilmDirectors(film.getId())));
        log.info("Top films received: {}", sortedFilms);
        return sortedFilms;
    }

    public Film addFilm(Film film) {
        if (filmStorage.containsFilmById(film.getId())) {
            throw new DataNotFoundException("Add film failed: film already exists");
        }
        filmStorage.validateFilm(film, "Add");
        validateReleaseDate(film);
        film = filmStorage.addFilm(film);
        directorStorage.addFilmDirectors(film);
        log.info("Film added: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilmById(film.getId())) {
            throw new DataNotFoundException("Update film failed: Film not found");
        }
        filmStorage.validateFilm(film, "Update");
        directorStorage.deleteFilmDirectors(film);
        film = filmStorage.updateFilm(film);
        directorStorage.addFilmDirectors(film);
        log.info("Film updated: {}", film);
        return film;
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        films.forEach(film -> film.setDirectors(directorStorage.getFilmDirectors(film.getId())));
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
        film.setDirectors(directorStorage.getFilmDirectors(id));
        log.info("Film by id received: {}", film);
        return film;
    }

    public List<Film> getSortedFilmsByDirector(int directorId, List<String> sortBy) {
        if (!directorStorage.containsDirectorById(directorId)) {
            log.warn("Director with id={} doesn't exist", directorId);
            throw new DataNotFoundException("Director not found");
        }
        List<Film> films = filmStorage.getSortedFilmsByDirector(directorId, sortBy);
        films.forEach(film -> film.setDirectors(directorStorage.getFilmDirectors(film.getId())));
        log.info("Sorted films by director received: {}", films);
        return films;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> sortedFilms = filmStorage.getCommonFilms(userId, friendId).stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return sortedFilms;
    }
}
