package ru.yandex.practicum.filmorate.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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

    public Film addLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            log.info("Add like failed: Incorrect film id");
            throw new DataNotFoundException("Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            log.info("Add like failed: Incorrect user id");
            throw new DataNotFoundException("Incorrect user id");
        }
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        log.info("Like added: {}", filmStorage.getFilmById(filmId));
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(int filmId, int userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            log.info("Delete like failed: Incorrect film id");
            throw new DataNotFoundException("Incorrect film id");
        }
        if (!userStorage.containsUserById(userId)) {
            log.info("Delete like failed: Incorrect user id");
            throw new DataNotFoundException("Incorrect user id");
        }
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
        log.info("Like deleted: {}", filmStorage.getFilmById(filmId));
        return filmStorage.getFilmById(filmId);
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
            log.info("Update film failed: Film not found");
            throw new DataNotFoundException("Film not found");
        }
        log.info("Film updated: {}", film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        log.info("Films received: {}", filmStorage.getFilms());
        return filmStorage.getFilms();
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM)) {
            log.info("Validation failed: Incorrect release date");
            throw new ValidationException("Incorrect release date");
        }
    }

    public Film getFilmById(int id) {
        if (!filmStorage.containsFilmById(id)) {
            log.info("Get film by id failed: Incorrect film id");
            throw new DataNotFoundException("Incorrect film id");
        }
        log.info("Film by id received: {}", filmStorage.getFilmById(id));
        return filmStorage.getFilmById(id);
    }

}
