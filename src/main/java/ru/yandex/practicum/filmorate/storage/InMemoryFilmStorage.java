package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();

    private int currentId = 0;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        films.put(++currentId, film);
        film.setId(currentId);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id){
        return films.get(id);
    }
}
