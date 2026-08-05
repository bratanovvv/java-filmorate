package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MpaRatingService {

    private final MpaRatingRepository mpaRatingRepository;

    public MpaRating getMpaRating(int id) {
        return mpaRatingRepository.getById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MPA_RATING_NOT_FOUND, id));
    }

    public List<MpaRating> getMpaRatings() {
        return mpaRatingRepository.getAll();
    }
}
