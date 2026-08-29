package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dao.Review;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Test
    void shouldSaveReview() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());

        Review saved = reviewService.saveReview(validReview(user.getId(), film.getId()));

        assertNotNull(saved.getId());
        assertEquals(user.getId(), saved.getUserId());
        assertEquals(film.getId(), saved.getFilmId());
    }

    @Test
    void shouldNotChangeAuthorAndFilmOnUpdate() {
        Review saved = savedReview();
        User otherUser = userService.saveUser(validUser());
        Film otherFilm = filmService.saveFilm(validFilm());

        Review changes = new Review();
        changes.setId(saved.getId());
        changes.setContent("Новый текст");
        changes.setIsPositive(false);
        changes.setUserId(otherUser.getId());
        changes.setFilmId(otherFilm.getId());

        reviewService.updateReview(changes);

        Review updated = reviewService.getReview(saved.getId());
        assertEquals("Новый текст", updated.getContent());
        assertEquals(saved.getUserId(), updated.getUserId());
        assertEquals(saved.getFilmId(), updated.getFilmId());
    }

    // -------- 404 --------

    @Test
    void shouldThrowWhenReviewNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.getReview(999)
        );
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenAuthorNotFound() {
        Film film = filmService.saveFilm(validFilm());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.saveReview(validReview(999, film.getId()))
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenFilmNotFound() {
        User user = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.saveReview(validReview(user.getId(), 999))
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenRatingUnknownReview() {
        User voter = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.addLike(999, voter.getId())
        );
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenRatingFromUnknownUser() {
        Review review = savedReview();

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.addLike(review.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenRequestedFilmNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> reviewService.getReviews(999, 10)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- helpers --------

    private Review savedReview() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());
        return reviewService.saveReview(validReview(user.getId(), film.getId()));
    }

    private Review validReview(Integer userId, Integer filmId) {
        Review review = new Review();
        review.setContent("Отличный фильм");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);
        return review;
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);
        return film;
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
