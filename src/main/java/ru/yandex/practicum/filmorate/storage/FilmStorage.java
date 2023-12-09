package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(int id);

    boolean containsFilmById(int id);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    void validateFilm(Film film, String operation);

    void deleteFilm(int filmId);

    List<Film> getSortedFilmsByDirector(int directorId, List<String> sortBy);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year);

    List<Film> searchFilms(String query, String by);
}
