package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.User;
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

    // -------- FRIENDS --------

    @Test
    void shouldAddFriend() {
        User user = userService.saveUser(validUser());
        User friend = userService.saveUser(validUser());

        userService.addFriend(user.getId(), friend.getId());

        User foundUser = userService.getUser(user.getId());
        User foundFriend = userService.getUser(friend.getId());

        assertEquals(1, foundUser.getFriends().size());
        assertTrue(foundUser.getFriends().contains(friend.getId()));
        assertEquals(1, foundFriend.getFriends().size());
        assertTrue(foundFriend.getFriends().contains(user.getId()));
    }

    @Test
    void shouldRemoveFriend() {
        User user = userService.saveUser(validUser());
        User friend = userService.saveUser(validUser());
        userService.addFriend(user.getId(), friend.getId());

        userService.removeFriend(user.getId(), friend.getId());

        User foundUser = userService.getUser(user.getId());
        User foundFriend = userService.getUser(friend.getId());

        assertTrue(foundUser.getFriends().isEmpty());
        assertTrue(foundFriend.getFriends().isEmpty());
    }

    @Test
    void shouldBeIdempotentWhenRemovingNonExistentFriend() {
        User user = userService.saveUser(validUser());
        User friend = userService.saveUser(validUser());

        userService.removeFriend(user.getId(), friend.getId());

        User foundUser = userService.getUser(user.getId());
        assertTrue(foundUser.getFriends().isEmpty());
    }

    @Test
    void shouldThrowWhenAddFriendUserNotFound() {
        User friend = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> userService.addFriend(999, friend.getId())
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenAddFriendFriendNotFound() {
        User user = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> userService.addFriend(user.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldReturnUserFriends() {
        User user = userService.saveUser(validUser());
        User friend1 = userService.saveUser(validUser());
        User friend2 = userService.saveUser(validUser());
        userService.addFriend(user.getId(), friend1.getId());
        userService.addFriend(user.getId(), friend2.getId());

        List<User> friends = userService.getUserFriends(user.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(f -> f.getId().equals(friend1.getId())));
        assertTrue(friends.stream().anyMatch(f -> f.getId().equals(friend2.getId())));
    }

    @Test
    void shouldReturnEmptyFriendsList() {
        User user = userService.saveUser(validUser());

        List<User> friends = userService.getUserFriends(user.getId());

        assertTrue(friends.isEmpty());
    }

    @Test
    void shouldReturnCommonFriends() {
        User user = userService.saveUser(validUser());
        User other = userService.saveUser(validUser());
        User commonFriend = userService.saveUser(validUser());
        User onlyUserFriend = userService.saveUser(validUser());
        userService.addFriend(user.getId(), commonFriend.getId());
        userService.addFriend(user.getId(), onlyUserFriend.getId());
        userService.addFriend(other.getId(), commonFriend.getId());

        List<User> common = userService.getCommonFriends(user.getId(), other.getId());

        assertEquals(1, common.size());
        assertEquals(commonFriend.getId(), common.get(0).getId());
    }

    @Test
    void shouldReturnEmptyCommonFriends() {
        User user = userService.saveUser(validUser());
        User other = userService.saveUser(validUser());

        List<User> common = userService.getCommonFriends(user.getId(), other.getId());

        assertTrue(common.isEmpty());
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