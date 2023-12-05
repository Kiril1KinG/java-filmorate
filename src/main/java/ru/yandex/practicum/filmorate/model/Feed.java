package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Feed {

    private int eventId;
    @JsonProperty("timestamp")
    private Long time;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

}
