package ru.yandex.practicum.filmorate.repository.impl.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class EventQueries {

    public static final String INSERT = """
            INSERT INTO events (user_id, entity_id, event_type, operation, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;

    public static final String FIND_FEED = """
            SELECT id, user_id, entity_id, event_type, operation, created_at
            FROM events
            WHERE user_id = ?
               OR user_id IN (SELECT friend_id FROM friendships WHERE user_id = ?)
            ORDER BY created_at ASC, id DESC
            """;
}