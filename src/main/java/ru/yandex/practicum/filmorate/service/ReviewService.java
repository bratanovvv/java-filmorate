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

    public List<Review> getReviews() {
        return reviewRepository.getAll();
    }

    @Transactional
    public Review saveReview(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());

        Review saved = reviewRepository.save(review);

        log.info("Создан отзыв: id={}, filmId={}, userId={}", saved.getId(),
                saved.getFilmId(), saved.getUserId());
        return saved;
    }
}
