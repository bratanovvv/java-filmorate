package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserService userService;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;

    public Film getFilm(int id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.FILM_NOT_FOUND, id));
    }

    public List<Film> getFilms() {
        return filmRepository.getAll();
    }

    @Transactional
    public Film saveFilm(Film film) {
        validateExistingFilm(film);
        Film saved = filmRepository.save(film);

        log.info("Создан фильм: id={}, name={}", saved.getId(), saved.getName());

        return saved;
    }

    @Transactional
    public Film updateFilm(Film film) {
        Film existingFilm = getFilm(film.getId());
        validateExistingFilm(film);

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setMpa(film.getMpa());
        existingFilm.getGenres().clear();
        existingFilm.getGenres().addAll(film.getGenres());

        log.info("Обновлён фильм: id={}, name={}", existingFilm.getId(), existingFilm.getName());

        return filmRepository.update(existingFilm);
    }

    @Transactional
    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);

        film.getLikes().add(userId);
        filmRepository.update(film);

        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    @Transactional
    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        validateExistingUser(userId);

        film.getLikes().remove(userId);
        filmRepository.update(film);

        log.info("Пользователь id={} убрал лайк с фильма id={}", userId, filmId);
    }

    public List<Film> popular(int count, Long genreId, Integer year) {
        log.info("Запрос популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);

        List<Film> films;
        films = filmRepository.findPopularByGenreAndYear(count, genreId, year);

        return films;
    }

    public void deleteFilm(int id) {
        Film film = getFilm(id);
        filmRepository.delete(film.getId());
    }

    private void validateExistingFilm(Film film) {
        if (film.getMpa() != null) {
            mpaRatingService.getMpaRating(film.getMpa().getId());
        }
        for (var genre : film.getGenres()) {
            genreService.getGenre(genre.getId());
        }
    }

    private void validateExistingUser(int userId) {
        userService.getUser(userId);
    }
}
