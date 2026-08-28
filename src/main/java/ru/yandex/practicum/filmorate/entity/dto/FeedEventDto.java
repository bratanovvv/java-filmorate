package ru.yandex.practicum.filmorate.entity.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;

@Data
public class FeedEventDto {

    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Long eventId;
    private Long entityId;
}