package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.EventQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository extends AbstractRepository<Integer, Event> {

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> rowMapper) {
        super(jdbc, rowMapper);
    }

    public Event save(Event event) {
        int id = insert(EventQueries.INSERT,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp());
        event.setId(id);
        return event;
    }

    public List<Event> findFeedForUser(int userId) {
        return findAll(EventQueries.FIND_FEED, userId, userId);
    }

    @Override
    public Optional<Event> getById(Integer id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Event> getAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Event update(Event event) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Event> findAllByIds(Collection<Integer> ids) {
        throw new UnsupportedOperationException("Not implemented");
    }
}