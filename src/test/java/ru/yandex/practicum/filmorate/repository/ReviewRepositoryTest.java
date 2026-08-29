package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dao.Review;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;
import ru.yandex.practicum.filmorate.repository.impl.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewRepositoryTest {

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbc;

    @Test
    void shouldSaveAndFindReviewByIdWithZeroUseful() {
        Film film = filmRepository.save(validFilm());
        User user = userRepository.save(validUser());

        Review saved = reviewRepository.save(validReview(user.getId(), film.getId()));

        assertThat(reviewRepository.getById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(r -> {
                    assertThat(r.getContent()).isEqualTo("Отличный фильм");
                    assertThat(r.getIsPositive()).isTrue();
                    assertThat(r.getUserId()).isEqualTo(user.getId());
                    assertThat(r.getFilmId()).isEqualTo(film.getId());
                    assertThat(r.getUseful()).isZero();
                });
    }

    @Test
    void shouldUpdateContentAndIsPositive() {
        Review saved = savedReview();

        saved.setContent("Передумал, фильм так себе");
        saved.setIsPositive(false);
        reviewRepository.update(saved);

        assertThat(reviewRepository.getById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(r -> {
                    assertThat(r.getContent()).isEqualTo("Передумал, фильм так себе");
                    assertThat(r.getIsPositive()).isFalse();
                });
    }

    @Test
    void shouldDeleteReviewWithItsRatings() {
        Review review = savedReview();
        User voter = userRepository.save(validUser());
        reviewRepository.addRating(review.getId(), voter.getId(), true);

        reviewRepository.delete(review.getId());

        assertThat(reviewRepository.getById(review.getId())).isEmpty();
        assertThat(ratingRows(review.getId())).isZero();
    }

    @Test
    void shouldCountUsefulAsLikesMinusDislikes() {
        Review review = savedReview();
        User u1 = userRepository.save(validUser());
        User u2 = userRepository.save(validUser());
        User u3 = userRepository.save(validUser());

        reviewRepository.addRating(review.getId(), u1.getId(), true);
        reviewRepository.addRating(review.getId(), u2.getId(), true);
        reviewRepository.addRating(review.getId(), u3.getId(), false);

        assertThat(useful(review)).isEqualTo(1);
    }

    @Test
    void shouldSwitchLikeToDislikeWithoutDuplicatingRow() {
        Review review = savedReview();
        User voter = userRepository.save(validUser());

        reviewRepository.addRating(review.getId(), voter.getId(), true);
        assertThat(useful(review)).isEqualTo(1);

        reviewRepository.addRating(review.getId(), voter.getId(), false);

        assertThat(useful(review)).isEqualTo(-1);
        assertThat(ratingRows(review.getId())).isEqualTo(1);
    }

    @Test
    void shouldRemoveOnlyOwnRating() {
        Review review = savedReview();
        User liker = userRepository.save(validUser());
        User disliker = userRepository.save(validUser());

        reviewRepository.addRating(review.getId(), liker.getId(), true);
        reviewRepository.addRating(review.getId(), disliker.getId(), false);

        reviewRepository.removeRating(review.getId(), liker.getId());

        assertThat(useful(review)).isEqualTo(-1);

        reviewRepository.removeRating(review.getId(), disliker.getId());

        assertThat(useful(review)).isZero();
    }

    @Test
    void shouldReturnFilmReviewsOrderedByUseful() {
        Film film = filmRepository.save(validFilm());
        User author = userRepository.save(validUser());
        User voter = userRepository.save(validUser());

        Review disliked = reviewRepository.save(validReview(author.getId(), film.getId()));
        Review liked = reviewRepository.save(validReview(author.getId(), film.getId()));

        reviewRepository.addRating(liked.getId(), voter.getId(), true);
        reviewRepository.addRating(disliked.getId(), voter.getId(), false);

        assertThat(reviewRepository.findByFilmId(film.getId(), 10))
                .extracting(Review::getId)
                .containsExactly(liked.getId(), disliked.getId());
    }

    @Test
    void shouldReturnOnlyReviewsOfRequestedFilm() {
        Film film = filmRepository.save(validFilm());
        Film otherFilm = filmRepository.save(validFilm());
        User author = userRepository.save(validUser());

        Review target = reviewRepository.save(validReview(author.getId(), film.getId()));
        reviewRepository.save(validReview(author.getId(), otherFilm.getId()));

        assertThat(reviewRepository.findByFilmId(film.getId(), 10))
                .extracting(Review::getId)
                .containsExactly(target.getId());
    }

    @Test
    void shouldLimitReviewsByCount() {
        Film film = filmRepository.save(validFilm());
        User author = userRepository.save(validUser());

        reviewRepository.save(validReview(author.getId(), film.getId()));
        reviewRepository.save(validReview(author.getId(), film.getId()));
        reviewRepository.save(validReview(author.getId(), film.getId()));

        assertThat(reviewRepository.findByFilmId(film.getId(), 2)).hasSize(2);
    }

    @Test
    void shouldReturnReviewsOfAllFilmsWhenFilmIdIsNull() {
        Film film = filmRepository.save(validFilm());
        Film otherFilm = filmRepository.save(validFilm());
        User author = userRepository.save(validUser());

        Review first = reviewRepository.save(validReview(author.getId(), film.getId()));
        Review second = reviewRepository.save(validReview(author.getId(), otherFilm.getId()));

        assertThat(reviewRepository.findByFilmId(null, 10))
                .hasSize(2)
                .extracting(Review::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void shouldCascadeDeleteReviewsWhenFilmOrAuthorDeleted() {
        Film film = filmRepository.save(validFilm());
        User author = userRepository.save(validUser());
        Review byFilm = reviewRepository.save(validReview(author.getId(), film.getId()));

        filmRepository.delete(film.getId());
        assertThat(reviewRepository.getById(byFilm.getId())).isEmpty();

        Film otherFilm = filmRepository.save(validFilm());
        User otherAuthor = userRepository.save(validUser());
        Review byAuthor = reviewRepository.save(
                validReview(otherAuthor.getId(), otherFilm.getId()));

        userRepository.delete(otherAuthor.getId());
        assertThat(reviewRepository.getById(byAuthor.getId())).isEmpty();
    }

    // -------- helpers --------

    private int useful(Review review) {
        Optional<Review> loaded = reviewRepository.getById(review.getId());
        return loaded.orElseThrow().getUseful();
    }

    private int ratingRows(int reviewId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM review_likes WHERE review_id = ?", Integer.class, reviewId);
        return count == null ? 0 : count;
    }

    private Review savedReview() {
        Film film = filmRepository.save(validFilm());
        User user = userRepository.save(validUser());
        return reviewRepository.save(validReview(user.getId(), film.getId()));
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
