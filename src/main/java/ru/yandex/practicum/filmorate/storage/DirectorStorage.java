package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> getDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    boolean containsDirectorById(int id);

    Set<Director> getFilmDirectors(int filmId);

    void addFilmDirectors(Film film);

    void deleteFilmDirectors(Film film);

}
