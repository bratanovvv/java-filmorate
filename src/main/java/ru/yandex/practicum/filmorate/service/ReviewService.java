package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Review;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.repository.impl.ReviewRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserService userService;
    private final FilmService filmService;
    private final ReviewRepository reviewRepository;

    public Review getReview(int id) {
        return reviewRepository.getById(id)
                .orElseThrow(() ->
                        new ApiException(ErrorCode.REVIEW_NOT_FOUND, id));
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId != null) {
            filmService.checkFilmExists(filmId);
        }
        return reviewRepository.findByFilmId(filmId, count);
    }

    @Transactional
    public Review saveReview(Review review) {
        userService.checkUserExists(review.getUserId());
        filmService.checkFilmExists(review.getFilmId());

        Review saved = reviewRepository.save(review);

        log.info("Создан отзыв: id={}, filmId={}, userId={}", saved.getId(), saved.getFilmId(), saved.getUserId());
        return saved;
    }

    @Transactional
    public void deleteReview(int reviewId) {
        checkReviewExists(reviewId);
        reviewRepository.delete(reviewId);
    }

    @Transactional
    public Review updateReview(Review review) {
        Review existingReview = getReview(review.getId());
        existingReview.setContent(review.getContent());
        existingReview.setIsPositive(review.getIsPositive());

        Review updated = reviewRepository.update(existingReview);
        log.info("Обновлён отзыв: id={}", updated.getId());

        return updated;
    }

    public void addLike(int reviewId, int userId) {
        checkReviewExists(reviewId);
        userService.checkUserExists(userId);

        reviewRepository.addRating(reviewId, userId, true);
        log.info("Пользователь id={} оценил отзыв id={} как полезный", userId, reviewId);
    }

    @Transactional
    public void addDislike(int reviewId, int userId) {
        checkReviewExists(reviewId);
        userService.checkUserExists(userId);

        reviewRepository.addRating(reviewId, userId, false);
        log.info("Пользователь id={} оценил отзыв id={} как бесполезный", userId, reviewId);
    }

    @Transactional
    public void removeRating(int reviewId, int userId) {
        checkReviewExists(reviewId);
        userService.checkUserExists(userId);

        reviewRepository.removeRating(reviewId, userId);
        log.info("Пользователь id={} снял оценку с отзыва id={}", userId, reviewId);
    }

    public void checkReviewExists(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ApiException(ErrorCode.REVIEW_NOT_FOUND, reviewId);
        }
    }
}
