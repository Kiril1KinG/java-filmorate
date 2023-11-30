package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.Service.GenreService;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("GET: /genres");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreVyId(@PathVariable int id) {
        log.info("GET: /genres/{}", id);
        return genreService.getGenreById(id);
    }
}
