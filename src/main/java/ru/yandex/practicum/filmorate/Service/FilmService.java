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
import java.util.ArrayList;
import java.util.List;

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

    public List<Film> getTopFilms(Integer count) {
        if (count == null) count = 10;
        if (count > filmStorage.getFilms().size()) {
            count = filmStorage.getFilms().size();
        }
        ArrayList<Film> sortedFilms = new ArrayList<>();
        for (Film film : filmStorage.getFilms()) {
            if (sortedFilms.isEmpty()) {
                sortedFilms.add(film);
                continue;
            }
            if (film.getLikes().size() > sortedFilms.get(0).getLikes().size()) {
                sortedFilms.add(0, film);
            } else {
                sortedFilms.add(film);
            }
        }
        log.info("Top films received: {}", sortedFilms.subList(0, count));
        return sortedFilms.subList(0, count);
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        log.info("Film added: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilm(film)) {
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
        if (filmStorage.getFilms().size() < id) {
            log.info("Get film by id failed: Incorrect film id");
            throw new DataNotFoundException("Incorrect film id");
        }
        log.info("Film by id received: {}", filmStorage.getFilmById(id));
        return filmStorage.getFilmById(id);
    }

}
