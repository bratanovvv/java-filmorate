package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.entity.dto.UserDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.model.base.BaseValidationEntityTest;

import java.time.LocalDate;

class UserValidationTest extends BaseValidationEntityTest<UserDto> {

    @Override
    protected UserDto createValidEntity() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Override
    protected Class<?> getValidationGroup() {
        return ValidationGroups.Create.class;
    }

    @Test
    void shouldPassValidationForValidUser() {
        UserDto user = createValidEntity();
        assertValid(user);
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        UserDto user = createValidEntity();
        user.setEmail("bad-email");

        assertViolationOnField(user, "email");
    }

    @Test
    void shouldFailWhenLoginBlank() {
        UserDto user = createValidEntity();
        user.setLogin("");

        assertViolationOnField(user, "login");
    }

    @Test
    void shouldFailWhenLoginHasSpaces() {
        UserDto user = createValidEntity();
        user.setLogin("bad login");

        assertViolationOnField(user, "login");
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        UserDto user = createValidEntity();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertViolationOnField(user, "birthday");
    }
}