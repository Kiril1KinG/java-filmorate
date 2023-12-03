package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Feed {

    private int eventId;
    private LocalDateTime time;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

}
