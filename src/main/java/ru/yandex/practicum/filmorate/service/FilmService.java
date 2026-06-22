package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public Film getFilm(int id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FILM_NOT_FOUND, id));
    }

    public List<Film> getFilms() {
        return filmRepository.getAll();
    }

    public Film saveFilm(Film film) {
        Film saved = filmRepository.save(film);

        log.info("Создан фильм: id={}, name={}", saved.getId(), saved.getName());

        return saved;
    }

    public Film updateFilm(Film film) {
        Film existingFilm = getFilm(film.getId());

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());

        log.info("Обновлён фильм: id={}, name={}", existingFilm.getId(), existingFilm.getName());

        return filmRepository.save(existingFilm);
    }
}
