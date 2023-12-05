package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingDbStorage ratingDbStorage;

    @GetMapping
    public List<Mpa> getAllRatings() {
        log.info("GET: /mpa");
        return ratingDbStorage.getAllRatings();
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable int id) {
        log.info("GET: /mpa/{}", id);
        return ratingDbStorage.getRatingById(id);
    }
}
