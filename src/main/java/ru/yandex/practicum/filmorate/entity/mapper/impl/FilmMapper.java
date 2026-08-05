package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dto.FilmDto;
import ru.yandex.practicum.filmorate.entity.dto.GenreDto;
import ru.yandex.practicum.filmorate.entity.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

import java.util.stream.Collectors;

@Component
public class FilmMapper implements Mapper<FilmDto, Film> {

    @Override
    public FilmDto toDto(Film film) {
        if (film == null) {
            return null;
        }
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(toMpaDto(film.getMpa()));
        dto.setGenres(film.getGenres().stream()
                .map(this::toGenreDto)
                .collect(Collectors.toSet()));
        return dto;
    }

    @Override
    public Film toEntity(FilmDto dto) {
        if (dto == null) {
            return null;
        }
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpa(toMpaEntity(dto.getMpa()));
        film.getGenres().addAll(dto.getGenres().stream()
                .map(this::toGenreEntity)
                .collect(Collectors.toSet()));
        return film;
    }

    private MpaRatingDto toMpaDto(MpaRating mpa) {
        if (mpa == null) {
            return null;
        }
        MpaRatingDto dto = new MpaRatingDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }

    private MpaRating toMpaEntity(MpaRatingDto dto) {
        if (dto == null) {
            return null;
        }
        MpaRating mpa = new MpaRating();
        mpa.setId(dto.getId());
        mpa.setName(dto.getName());
        return mpa;
    }

    private GenreDto toGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    private Genre toGenreEntity(GenreDto dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }
}
