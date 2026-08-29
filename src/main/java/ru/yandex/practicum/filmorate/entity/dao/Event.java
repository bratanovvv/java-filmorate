package ru.yandex.practicum.filmorate.entity.dao;

import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;

@Data
public class Event {

    private Integer id;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer entityId;
}