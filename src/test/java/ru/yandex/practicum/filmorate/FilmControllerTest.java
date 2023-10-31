package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void filmWithIncorrectReleaseDateShouldReturnException() {
        Film film1 = new Film(1, "name", "desc", LocalDate.of(1800, 12, 4), 100L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film1));
    }

    @Test
    public void MethodAddFilmShouldReturnExpectedFilm() {
        Film film1 = new Film(1, "name", "desc", LocalDate.of(1900, 12, 4), 100L);
        Film expected = new Film(1, "name", "desc", LocalDate.of(1900, 12, 4), 100L);
        Assertions.assertEquals(filmController.addFilm(film1), expected);
    }
}