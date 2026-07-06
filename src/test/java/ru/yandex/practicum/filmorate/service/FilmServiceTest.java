package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmRepository filmRepository;

    @BeforeEach
    void setUp() {
        filmRepository.clear();
    }

    // -------- CREATE --------

    @Test
    void shouldSaveFilm() {
        Film film = validFilm();

        Film saved = filmService.saveFilm(film);

        assertNotNull(saved.getId());
        assertEquals("Matrix", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(LocalDate.of(1999, 3, 31), saved.getReleaseDate());
        assertEquals(136, saved.getDuration());
    }

    // -------- GET --------

    @Test
    void shouldFindSavedFilm() {
        Film saved = filmService.saveFilm(validFilm());

        Film found = filmService.getFilm(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getName(), found.getName());
        assertEquals(saved.getDescription(), found.getDescription());
        assertEquals(saved.getReleaseDate(), found.getReleaseDate());
        assertEquals(saved.getDuration(), found.getDuration());
    }

    @Test
    void shouldThrowWhenFilmNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getFilm(999)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- UPDATE --------

    @Test
    void shouldUpdateFilm() {
        Film saved = filmService.saveFilm(validFilm());

        Film update = validFilm();
        update.setId(saved.getId());
        update.setName("New name");
        update.setDescription("New description");
        update.setReleaseDate(LocalDate.of(2020, 1, 1));
        update.setDuration(120);

        Film updated = filmService.updateFilm(update);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("New name", updated.getName());
        assertEquals("New description", updated.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), updated.getReleaseDate());
        assertEquals(120, updated.getDuration());
    }

    @Test
    void shouldThrowWhenUpdatingUnknownFilm() {
        Film film = validFilm();
        film.setId(999);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.updateFilm(film)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- LIST --------

    @Test
    void shouldReturnEmptyList() {
        List<Film> films = filmService.getFilms();

        assertTrue(films.isEmpty());
    }

    @Test
    void shouldReturnAllFilms() {
        filmService.saveFilm(validFilm());
        filmService.saveFilm(validFilm());

        List<Film> films = filmService.getFilms();

        assertEquals(2, films.size());
    }

    // -------- helper --------

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        return film;
    }
}