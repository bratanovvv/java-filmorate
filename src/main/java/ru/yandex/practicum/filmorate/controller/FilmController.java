package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.util.FilmSortOption;
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
    public List<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = DEFAULT_POPULAR_COUNT) @Positive int count,
            @RequestParam(required = false) @Positive Long genreId,
            @RequestParam(required = false) @Min(1895) @Max(2100) Integer year
    ) {
        List<Film> films = filmService.popular(count, genreId, year);
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectorFilms(@PathVariable int directorId,
                                          @RequestParam(defaultValue = "year") FilmSortOption sortBy) {
        List<Film> films = filmService.getFilmsByDirector(directorId, sortBy);
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        return commonFilms.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam(required = false) String query,
                                     @RequestParam(defaultValue = "title") String by) {
        List<Film> films = filmService.search(query, by);
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }
}
