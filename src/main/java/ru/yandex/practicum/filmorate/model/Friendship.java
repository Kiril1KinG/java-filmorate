package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class Friendship {

    private final int userId;
    private final int friendId;
    private final boolean friendshipStatus;
}
