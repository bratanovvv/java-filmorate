package ru.yandex.practicum.filmorate.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dto.FilmDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private static final String DEFAULT_POPULAR_COUNT = "10";

    private final FilmService filmService;
    private final Mapper<FilmDto, Film> filmMapper;

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable int id) {
        Film film = filmService.getFilm(id);
        return filmMapper.toDto(film);
    }

    @GetMapping
    public List<FilmDto> getFilms() {
        List<Film> films = filmService.getFilms();
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FilmDto createFilm(@Validated(ValidationGroups.Create.class) @RequestBody FilmDto filmDto) {
        filmDto.setId(null);
        Film film = filmMapper.toEntity(filmDto);
        Film saved = filmService.saveFilm(film);
        return filmMapper.toDto(saved);
    }

    @PutMapping
    public FilmDto updateFilm(@Validated(ValidationGroups.Update.class) @RequestBody FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        Film updated = filmService.updateFilm(film);
        return filmMapper.toDto(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = DEFAULT_POPULAR_COUNT) int count) {
        List<Film> films = filmService.getPopularFilms(count);
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }
}
