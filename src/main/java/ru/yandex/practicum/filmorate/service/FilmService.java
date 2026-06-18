package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public Film getFilm(int id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id=" + id + " не найден"));
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
