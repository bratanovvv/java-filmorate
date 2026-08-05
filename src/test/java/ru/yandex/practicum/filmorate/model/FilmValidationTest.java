package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.model.base.BaseValidationEntityTest;

import java.time.LocalDate;


class FilmValidationTest extends BaseValidationEntityTest<Film> {

    @Override
    protected Film createValidEntity() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        return film;
    }

    @Test
    void shouldPassValidationForValidFilm() {
        Film film = createValidEntity();
        assertValid(film);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = createValidEntity();
        film.setName("");

        assertViolationOnField(film, "name");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = createValidEntity();
        film.setDescription("a".repeat(201));

        assertViolationOnField(film, "description");
    }

    @Test
    void shouldFailWhenReleaseDateInvalid() {
        Film film = createValidEntity();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertViolationOnField(film, "releaseDate");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = createValidEntity();
        film.setDuration(0);

        assertViolationOnField(film, "duration");
    }
}
