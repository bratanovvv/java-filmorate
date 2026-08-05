package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

@Component
public class MpaRatingMapper implements Mapper<MpaRatingDto, MpaRating> {

    @Override
    public MpaRatingDto toDto(MpaRating mpaRating) {
        if (mpaRating == null) {
            return null;
        }
        MpaRatingDto dto = new MpaRatingDto();
        dto.setId(mpaRating.getId());
        dto.setName(mpaRating.getName());
        return dto;
    }

    @Override
    public MpaRating toEntity(MpaRatingDto dto) {
        if (dto == null) {
            return null;
        }
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(dto.getId());
        mpaRating.setName(dto.getName());
        return mpaRating;
    }
}
