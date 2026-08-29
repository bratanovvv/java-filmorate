package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.entity.dto.FeedEventDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

@Component
public class EventMapper implements Mapper<FeedEventDto, Event> {

    @Override
    public Event toEntity(FeedEventDto dto) {
        if (dto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(dto.getEventId() != null ? dto.getEventId().intValue() : null);
        event.setTimestamp(dto.getTimestamp());
        event.setUserId(dto.getUserId());
        event.setEventType(dto.getEventType());
        event.setOperation(dto.getOperation());
        event.setEntityId(dto.getEntityId() != null ? dto.getEntityId().intValue() : null);
        return event;
    }

    @Override
    public FeedEventDto toDto(Event event) {
        if (event == null) {
            return null;
        }
        FeedEventDto dto = new FeedEventDto();
        dto.setTimestamp(event.getTimestamp());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType());
        dto.setOperation(event.getOperation());
        dto.setEventId(event.getId() != null ? event.getId().longValue() : null);
        dto.setEntityId(event.getEntityId() != null ? event.getEntityId().longValue() : null);
        return dto;
    }
}