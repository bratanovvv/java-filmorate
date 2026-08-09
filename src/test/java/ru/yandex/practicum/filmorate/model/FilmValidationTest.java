package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.entity.dto.FilmDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.model.base.BaseValidationEntityTest;

import java.time.LocalDate;


class FilmValidationTest extends BaseValidationEntityTest<FilmDto> {

    @Override
    protected FilmDto createValidEntity() {
        FilmDto film = new FilmDto();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        return film;
    }

    @Override
    protected Class<?> getValidationGroup() {
        return ValidationGroups.Create.class;
    }

    @Test
    void shouldPassValidationForValidFilm() {
        FilmDto film = createValidEntity();
        assertValid(film);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        FilmDto film = createValidEntity();
        film.setName("");

        assertViolationOnField(film, "name");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        FilmDto film = createValidEntity();
        film.setDescription("a".repeat(201));

        assertViolationOnField(film, "description");
    }

    @Test
    void shouldFailWhenReleaseDateInvalid() {
        FilmDto film = createValidEntity();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertViolationOnField(film, "releaseDate");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        FilmDto film = createValidEntity();
        film.setDuration(0);

        assertViolationOnField(film, "duration");
    }
}