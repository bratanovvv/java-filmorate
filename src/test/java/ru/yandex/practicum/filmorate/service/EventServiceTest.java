package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Event;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.entity.dao.util.EventOperation;
import ru.yandex.practicum.filmorate.entity.dao.util.EventType;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Test
    void shouldReturnOnlyOwnEvents() {
        User owner = userService.saveUser(validUser());
        User friend = userService.saveUser(validUser());
        User stranger = userService.saveUser(validUser());

        userService.addFriend(owner.getId(), friend.getId());
        eventService.record(EventType.LIKE, EventOperation.ADD, friend.getId(), 5);
        eventService.record(EventType.REVIEW, EventOperation.ADD, stranger.getId(), 7);

        List<Event> feed = userService.getUserFeed(owner.getId());

        assertThat(feed).hasSize(1);
        assertThat(feed)
                .singleElement()
                .satisfies(e -> assertThat(e.getUserId()).isEqualTo(owner.getId()));
        assertThat(feed).noneMatch(e -> e.getUserId().equals(friend.getId()));
        assertThat(feed).noneMatch(e -> e.getUserId().equals(stranger.getId()));
    }

    @Test
    void shouldReturnEmptyFeedWhenNoEvents() {
        User user = userService.saveUser(validUser());

        assertThat(userService.getUserFeed(user.getId())).isEmpty();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> userService.getUserFeed(999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    private User validUser() {
        User user = new User();
        user.setEmail("test-" + UUID.randomUUID() + "@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}