package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dto.FilmDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Component
public class FilmMapper implements Mapper<FilmDto, Film> {

    private final MpaRatingMapper mpaRatingMapper;
    private final GenreMapper genreMapper;
    private final DirectorMapper directorMapper;

    public FilmMapper(MpaRatingMapper mpaRatingMapper, GenreMapper genreMapper, DirectorMapper directorMapper) {
        this.mpaRatingMapper = mpaRatingMapper;
        this.genreMapper = genreMapper;
        this.directorMapper = directorMapper;
    }

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
        dto.setMpa(mpaRatingMapper.toDto(film.getMpa()));
        dto.setGenres(film.getGenres().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        dto.setDirectors(film.getDirectors().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
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
        film.setMpa(mpaRatingMapper.toEntity(dto.getMpa()));
        film.getGenres().addAll(dto.getGenres().stream()
                .map(genreMapper::toEntity)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        film.getDirectors().addAll(dto.getDirectors().stream()
                .map(directorMapper::toEntity)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return film;
    }
}
