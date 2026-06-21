package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.base.BaseValidationEntityTest;

import java.time.LocalDate;

class UserValidationTest extends BaseValidationEntityTest<User> {

    @Override
    protected User createValidEntity() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    void shouldPassValidationForValidUser() {
        User user = createValidEntity();
        assertValid(user);
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        User user = createValidEntity();
        user.setEmail("bad-email");

        assertViolationOnField(user, "email");
    }

    @Test
    void shouldFailWhenLoginBlank() {
        User user = createValidEntity();
        user.setLogin("");

        assertViolationOnField(user, "login");
    }

    @Test
    void shouldFailWhenLoginHasSpaces() {
        User user = createValidEntity();
        user.setLogin("bad login");

        assertViolationOnField(user, "login");
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = createValidEntity();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertViolationOnField(user, "birthday");
    }
}
