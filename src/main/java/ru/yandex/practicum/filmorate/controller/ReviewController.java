package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.dao.Review;

import ru.yandex.practicum.filmorate.entity.dto.ReviewDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final Mapper<ReviewDto, Review> reviewMapper;

    @GetMapping
    public List<ReviewDto> getReviews() {
        List<Review> reviews = reviewService.getReviews();
        return reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable int id) {
        Review review = reviewService.getReview(id);
        return reviewMapper.toDto(review);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReviewDto createReview(@Validated(ValidationGroups.Create.class) @RequestBody ReviewDto reviewDto) {
        reviewDto.setReviewId(null);
        Review review = reviewMapper.toEntity(reviewDto);
        Review saved = reviewService.saveReview(review);
        return reviewMapper.toDto(saved);
    }



/*  POST /reviews - Добавление нового отзыва
    PUT /reviews - Редактирование уже нежного отзыва.
    DELETE /reviews/{id} - Удаление уже окрашенного отзыва.
    GET /reviews/{id} -  Получение отзыва по идентификатору.

    Получение всех рецензий по идентификатору фильма, если фильм не указан все.
    Если кол-во не указано то 10.
    GET /reviews?filmId={filmId}&count={count}

    PUT /reviews/{id}/like/{userId} — пользователь ставит лайк отзыву.
    PUT /reviews/{id}/dislike/{userId} — пользователь ставит дизлайк отзыву.
    DELETE /reviews/{id}/like/{userId} — пользователь удаляет лайк/дизлайк отзыву.
    DELETE /reviews/{id}/dislike/{userId} — пользователь удаляет дизлайк отзыву

    */
}
