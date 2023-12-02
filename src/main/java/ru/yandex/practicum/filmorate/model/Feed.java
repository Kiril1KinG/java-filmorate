package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Feed {

    private int id;
    private LocalDateTime time;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

}
