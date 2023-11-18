package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingDbStorage ratingDbStorage;

    @GetMapping
    public List<Mpa> getAllRatings() {
        return ratingDbStorage.getAllRatings();
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable int id) {
        return ratingDbStorage.getRatingById(id);
    }
}
