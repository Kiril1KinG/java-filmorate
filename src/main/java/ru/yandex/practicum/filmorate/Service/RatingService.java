package ru.yandex.practicum.filmorate.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingDbStorage ratingDbStorage;

    public List<Mpa> getAllRatings() {
        List<Mpa> mpas = ratingDbStorage.getAllRatings();
        log.info("Ratings received: {}", mpas);
        return mpas;
    }

    public Mpa getRatingById(int id) {
        Mpa mpa = ratingDbStorage.getRatingById(id);
        log.info("Rating by id received: {}", mpa);
        return mpa;
    }
}
