package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dto.GenreDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

@Component
public class GenreMapper implements Mapper<GenreDto, Genre> {

    @Override
    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    @Override
    public Genre toEntity(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }
}
