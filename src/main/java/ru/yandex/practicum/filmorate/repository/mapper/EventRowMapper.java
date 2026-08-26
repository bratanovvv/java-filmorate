package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setUserId(rs.getInt("user_id"));
        event.setEntityId(rs.getInt("entity_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(EventOperation.valueOf(rs.getString("operation")));
        event.setTimestamp(rs.getLong("created_at"));
        return event;
    }
}