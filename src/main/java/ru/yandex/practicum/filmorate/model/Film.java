package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Film {

    private int id;

    @NotEmpty
    private String name;

    @Length(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Min(1)
    private long duration;

    private Set<Integer> likes = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
