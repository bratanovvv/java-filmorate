package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;
    private final Mapper<MpaRatingDto, MpaRating> mpaRatingMapper;

    @GetMapping
    public List<MpaRatingDto> getMpaRatings() {
        return mpaRatingMapper.toDtoList(mpaRatingService.getMpaRatings());
    }

    @GetMapping("/{id}")
    public MpaRatingDto getMpaRating(@PathVariable int id) {
        MpaRating rating = mpaRatingService.getMpaRating(id);
        return mpaRatingMapper.toDto(rating);
    }
}
