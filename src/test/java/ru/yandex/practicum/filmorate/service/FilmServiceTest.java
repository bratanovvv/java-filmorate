package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

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

    // -------- LIKES --------

    @Test
    void shouldAddLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());

        filmService.addLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertEquals(1, likedFilm.getLikes().size());
        assertTrue(likedFilm.getLikes().contains(user.getId()));
    }

    @Test
    void shouldBeIdempotentWhenAddingDuplicateLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());

        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertEquals(1, likedFilm.getLikes().size());
    }

    @Test
    void shouldRemoveLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());
        filmService.addLike(film.getId(), user.getId());

        filmService.removeLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertTrue(likedFilm.getLikes().isEmpty());
    }

    @Test
    void shouldBeIdempotentWhenRemovingNonExistentLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());

        filmService.removeLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertTrue(likedFilm.getLikes().isEmpty());
    }

    @Test
    void shouldThrowWhenLikeFilmNotFound() {
        User user = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.addLike(999, user.getId())
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenLikeUserNotFound() {
        Film film = filmService.saveFilm(validFilm());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.addLike(film.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenRemoveLikeUserNotFound() {
        Film film = filmService.saveFilm(validFilm());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.removeLike(film.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldReturnPopularFilms() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> popular = filmService.getPopularFilms(2);

        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }

    @Test
    void shouldReturnAllFilmsWhenCountExceedsTotal() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user.getId());

        List<Film> popular = filmService.getPopularFilms(100);

        assertEquals(2, popular.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoFilms() {
        List<Film> popular = filmService.getPopularFilms(10);

        assertTrue(popular.isEmpty());
    }

    // -------- RECOMMENDATIONS --------

    @Test
    void shouldReturnRecommendationsFromSimilarUser() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(1, recommendations.size());
        assertEquals(film3.getId(), recommendations.get(0).getId());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoLikes() {
        Film film1 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoOverlapWithOtherUsers() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldExcludeFilmsAlreadyLikedByTargetUser() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film3.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldSelectOnlyTopSimilarUserFilms() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        Film film4 = filmService.saveFilm(validFilm());
        Film film5 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film4.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());
        filmService.addLike(film5.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertTrue(recommendations.stream().map(Film::getId).toList()
                .containsAll(List.of(film3.getId(), film4.getId())));
        assertFalse(recommendations.stream().map(Film::getId).toList().contains(film5.getId()));
    }

    @Test
    void shouldCombineRecommendationsFromTiedSimilarUsers() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        Film film4 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());
        filmService.addLike(film2.getId(), user3.getId());
        filmService.addLike(film4.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertTrue(recommendations.stream().map(Film::getId).toList()
                .containsAll(List.of(film3.getId(), film4.getId())));
    }

    @Test
    void shouldOrderRecommendationsByPopularity() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film3.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertEquals(film3.getId(), recommendations.get(0).getId());
        assertEquals(film2.getId(), recommendations.get(1).getId());
    }

    @Test
    void shouldThrowWhenRecommendationsForUnknownUser() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getRecommendations(999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    // -------- DELETE --------

    @Test
    void shouldDeleteFilm() {
        Film saved = filmService.saveFilm(validFilm());

        filmService.deleteFilm(saved.getId());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getFilm(saved.getId())
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenDeleteUnknownFilm() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.deleteFilm(999)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
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

    private User validUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}