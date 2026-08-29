package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User saved = userRepository.save(user);

        Optional<User> userOptional = userRepository.getById(saved.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", saved.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("email", "test@mail.com");
                    assertThat(u).hasFieldOrPropertyWithValue("login", "testlogin");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "Test User");
                    assertThat(u).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(2000, 1, 1));
                });
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        Optional<User> userOptional = userRepository.getById(999);

        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());

        List<User> users = userRepository.getAll();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .contains(user1.getId(), user2.getId());
    }

    @Test
    void shouldUpdateUser() {
        User saved = userRepository.save(validUser());

        saved.setEmail("new@mail.com");
        saved.setLogin("newlogin");
        saved.setName("New Name");
        saved.setBirthday(LocalDate.of(1995, 5, 5));

        userRepository.update(saved);

        Optional<User> updated = userRepository.getById(saved.getId());

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("email", "new@mail.com");
                    assertThat(u).hasFieldOrPropertyWithValue("login", "newlogin");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "New Name");
                    assertThat(u).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void shouldFindUsersByIds() {
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());

        List<User> found = userRepository.findAllByIds(
                List.of(user1.getId(), user2.getId()));

        assertThat(found)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void shouldDeleteUser() {
        User saved = userRepository.save(validUser());

        userRepository.delete(saved.getId());

        Optional<User> loaded = userRepository.getById(saved.getId());
        assertThat(loaded).isEmpty();
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