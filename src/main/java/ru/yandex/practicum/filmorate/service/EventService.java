package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;
import ru.yandex.practicum.filmorate.repository.impl.EventRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;

    protected void record(EventType eventType, EventOperation operation, int userId, int entityId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(System.currentTimeMillis());

        Event saved = eventRepository.save(event);
        log.info("Записано событие: id={}, type={}, operation={}, userId={}, entityId={}",
                saved.getId(), eventType, operation, userId, entityId);
    }

    protected List<Event> findFeedForUser(int userId) {
        return eventRepository.findFeedForUser(userId);
    }
}