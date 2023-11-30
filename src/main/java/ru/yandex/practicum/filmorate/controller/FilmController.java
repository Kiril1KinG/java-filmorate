package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;


    @GetMapping
    public List<Film> getFilms() {
        log.info("GET: /films");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("GET: /films/{}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("POST: /films");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("PUT: /films");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT: /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE: /films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("GET: /films/popular?count={}", count);
        return filmService.getTopFilms(count);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("GET: /films/search?query={}&by={}", query, by);
        return filmService.searchFilms(query, by);
    }
}
