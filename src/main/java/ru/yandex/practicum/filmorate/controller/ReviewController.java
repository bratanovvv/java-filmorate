package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.entity.dao.Review;

import ru.yandex.practicum.filmorate.entity.dto.ReviewDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final Mapper<ReviewDto, Review> reviewMapper;

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable int id) {
        Review review = reviewService.getReview(id);
        return reviewMapper.toDto(review);
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") @Positive int count) {
        return reviewService.getReviews(filmId, count).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReviewDto createReview(@Validated(ValidationGroups.Create.class) @RequestBody ReviewDto reviewDto) {
        reviewDto.setReviewId(null);
        Review review = reviewMapper.toEntity(reviewDto);
        Review saved = reviewService.saveReview(review);
        return reviewMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
    }

    @PutMapping
    public ReviewDto updateReview(@Validated(ValidationGroups.Update.class) @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review updated = reviewService.updateReview(review);
        return reviewMapper.toDto(updated);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeRating(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeRating(id, userId);
    }
}
