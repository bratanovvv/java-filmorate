package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Review;
import ru.yandex.practicum.filmorate.entity.dto.ReviewDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

@Component
public class ReviewMapper implements Mapper<ReviewDto, Review> {

    @Override
    public Review toEntity(ReviewDto reviewDto) {
        if (reviewDto == null) {
            return null;
        }
        Review review = new Review();
        review.setId(reviewDto.getReviewId());
        review.setContent(reviewDto.getContent());
        review.setIsPositive(reviewDto.getIsPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        review.setUseful(reviewDto.getUseful());
        return review;
    }

    @Override
    public ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());
        return reviewDto;
    }
}
