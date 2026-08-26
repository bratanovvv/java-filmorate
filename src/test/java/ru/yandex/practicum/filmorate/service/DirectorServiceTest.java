package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class DirectorServiceTest {

    @Autowired
    private DirectorService directorService;

    // -------- CREATE --------

    @Test
    void shouldSaveDirector() {
        Director director = validDirector();

        Director saved = directorService.saveDirector(director);

        assertNotNull(saved.getId());
        assertEquals("Christopher Nolan", saved.getName());
    }

    // -------- GET --------

    @Test
    void shouldFindSavedDirector() {
        Director saved = directorService.saveDirector(validDirector());

        Director found = directorService.getDirector(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getName(), found.getName());
    }

    @Test
    void shouldThrowWhenDirectorNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> directorService.getDirector(999)
        );
        assertEquals(ErrorCode.DIRECTOR_NOT_FOUND, ex.getCode());
    }

    // -------- UPDATE --------

    @Test
    void shouldUpdateDirector() {
        Director saved = directorService.saveDirector(validDirector());

        Director update = validDirector();
        update.setId(saved.getId());
        update.setName("Steven Spielberg");

        Director updated = directorService.updateDirector(update);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Steven Spielberg", updated.getName());
    }

    @Test
    void shouldThrowWhenUpdatingUnknownDirector() {
        Director director = validDirector();
        director.setId(999);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> directorService.updateDirector(director)
        );
        assertEquals(ErrorCode.DIRECTOR_NOT_FOUND, ex.getCode());
    }

    // -------- LIST --------

    @Test
    void shouldReturnEmptyList() {
        List<Director> directors = directorService.getDirectors();

        assertTrue(directors.isEmpty());
    }

    @Test
    void shouldReturnAllDirectors() {
        directorService.saveDirector(validDirector());
        directorService.saveDirector(validDirector());

        List<Director> directors = directorService.getDirectors();

        assertEquals(2, directors.size());
    }

    // -------- DELETE --------

    @Test
    void shouldDeleteDirector() {
        Director saved = directorService.saveDirector(validDirector());

        directorService.deleteDirector(saved.getId());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> directorService.getDirector(saved.getId())
        );
        assertEquals(ErrorCode.DIRECTOR_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenDeleteUnknownDirector() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> directorService.deleteDirector(999)
        );
        assertEquals(ErrorCode.DIRECTOR_NOT_FOUND, ex.getCode());
    }

    // -------- helper --------

    private Director validDirector() {
        Director director = new Director();
        director.setName("Christopher Nolan");
        return director;
    }
}
