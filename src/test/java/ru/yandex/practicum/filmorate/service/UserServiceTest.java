package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.clear();
    }

    // -------- CREATE --------

    @Test
    void shouldSaveUser() {
        User user = validUser();

        User saved = userService.saveUser(user);

        assertNotNull(saved.getId());
        assertEquals("testlogin", saved.getLogin());
        assertEquals("Test User", saved.getName()); // normalizeUser
    }

    @Test
    void shouldSaveNoNameUser() {
        User user = validUser();
        user.setName(null);

        User saved = userService.saveUser(user);

        assertNotNull(saved.getId());
        assertEquals("testlogin", saved.getLogin());
        assertEquals("testlogin", saved.getName()); // normalizeUser
    }

    // -------- GET --------

    @Test
    void shouldGetUserById() {
        User saved = userService.saveUser(validUser());

        User found = userService.getUser(saved.getId());

        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> userService.getUser(999)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    // -------- UPDATE --------

    @Test
    void shouldUpdateUser() {
        User saved = userService.saveUser(validUser());

        User update = new User();
        update.setId(saved.getId());
        update.setEmail("new@mail.com");
        update.setLogin("newlogin");
        update.setName(""); // проверка normalize
        update.setBirthday(LocalDate.of(1995, 1, 1));

        User updated = userService.updateUser(update);

        assertEquals("new@mail.com", updated.getEmail());
        assertEquals("newlogin", updated.getLogin());
        assertEquals("newlogin", updated.getName()); // normalizeUser
    }

    @Test
    void shouldThrowWhenUpdatingUnknownUser() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@mail.com");
        user.setLogin("login");

        ApiException ex = assertThrows(
                ApiException.class,
                () -> userService.updateUser(user)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    // -------- LIST --------

    @Test
    void shouldReturnAllUsers() {
        userService.saveUser(validUser());
        userService.saveUser(validUser());

        List<User> users = userService.getUsers();

        assertEquals(2, users.size());
    }

    @Test
    void shouldReturnEmptyList() {
        List<User> users = userService.getUsers();

        assertTrue(users.isEmpty());
    }

    // -------- helper --------

    private User validUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}