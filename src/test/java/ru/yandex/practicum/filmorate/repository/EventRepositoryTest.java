package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;
import ru.yandex.practicum.filmorate.repository.impl.EventRepository;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventRepositoryTest {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Test
    void shouldSaveAndReadEvent() {
        User user = userRepository.save(validUser());

        Event saved = eventRepository.save(event(EventType.LIKE, EventOperation.ADD, user.getId(), 10, 1L));

        assertThat(saved.getId()).isNotNull();
        assertThat(eventRepository.findFeedForUser(user.getId()))
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.getUserId()).isEqualTo(user.getId());
                    assertThat(e.getEntityId()).isEqualTo(10);
                    assertThat(e.getEventType()).isEqualTo(EventType.LIKE);
                    assertThat(e.getOperation()).isEqualTo(EventOperation.ADD);
                    assertThat(e.getTimestamp()).isEqualTo(1L);
                });
    }

    @Test
    void shouldReturnOwnAndFriendsEventsOnly() {
        User owner = userRepository.save(validUser());
        User friend = userRepository.save(validUser());
        User stranger = userRepository.save(validUser());

        makeFriends(owner.getId(), friend.getId());

        Event own = eventRepository.save(event(EventType.FRIEND, EventOperation.ADD, owner.getId(), friend.getId(), 1L));
        Event friendEvent = eventRepository.save(event(EventType.LIKE, EventOperation.ADD, friend.getId(), 5, 2L));
        eventRepository.save(event(EventType.REVIEW, EventOperation.ADD, stranger.getId(), 7, 3L));

        List<Event> feed = eventRepository.findFeedForUser(owner.getId());

        assertThat(feed)
                .extracting(Event::getId)
                .containsExactlyInAnyOrder(own.getId(), friendEvent.getId());
        assertThat(feed).noneMatch(e -> e.getUserId().equals(stranger.getId()));
    }

    @Test
    void shouldOrderEventsByTimestampAsscending() {
        User user = userRepository.save(validUser());

        Event first = eventRepository.save(event(EventType.LIKE, EventOperation.ADD, user.getId(), 1, 100L));
        Event second = eventRepository.save(event(EventType.LIKE, EventOperation.REMOVE, user.getId(), 2, 300L));
        Event third = eventRepository.save(event(EventType.FRIEND, EventOperation.ADD, user.getId(), 3, 200L));

        List<Event> feed = eventRepository.findFeedForUser(user.getId());

        assertThat(feed)
                .extracting(Event::getId)
                .containsExactly(first.getId(), third.getId(), second.getId());
    }

    // -------- helpers --------

    private void makeFriends(int userId, int friendId) {
        User user = userRepository.getById(userId).orElseThrow();
        user.getFriends().add(friendId);
        userRepository.update(user);
    }

    private Event event(EventType type, EventOperation operation, int userId, int entityId, long timestamp) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(type);
        event.setOperation(operation);
        event.setTimestamp(timestamp);
        return event;
    }

    private User validUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}