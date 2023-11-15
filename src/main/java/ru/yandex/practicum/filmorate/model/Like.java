package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class Like {

    private final int userId;
    private final int filmId;
}
