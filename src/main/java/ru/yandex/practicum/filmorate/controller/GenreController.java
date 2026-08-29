package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dto.GenreDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final Mapper<GenreDto, Genre> genreMapper;

    @GetMapping
    public List<GenreDto> getGenres() {
        return genreMapper.toDtoList(genreService.getGenres());
    }

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable int id) {
        Genre genre = genreService.getGenre(id);
        return genreMapper.toDto(genre);
    }
}
