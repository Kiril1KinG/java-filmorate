package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class Rating {

    private final String name;
}
