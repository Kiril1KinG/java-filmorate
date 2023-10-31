package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate FIRST_MOVIE = LocalDate.of(1895, 12, 28);

    Map<Integer, Film> films = new HashMap();

    private int currentId = 0;

    private boolean valid(Film film) {
        return !film.getReleaseDate().isBefore(FIRST_MOVIE);
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        if (valid(film)) {
            films.put(++currentId, film);
            film.setId(currentId);
            return film;
        } else {
            log.info("Ошибка валидации: Неккоректная дата выхода фильма");
            throw new ValidationException("Неккоректная дата выхода фильма");
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.info("Ошибка обновления: Фильм не найден");
            throw new DataNotFoundException("Фильм не найден");
        }
    }
}
