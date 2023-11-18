package ru.yandex.practicum.filmorate.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        log.info("Genres received: {}", genres);
        return genres;
    }

    public Genre getGenreById(int id) {
        Genre genre = genreDbStorage.getGenreById(id);
        log.info("Genre by id received: {}", genre);
        return genre;
    }

}
