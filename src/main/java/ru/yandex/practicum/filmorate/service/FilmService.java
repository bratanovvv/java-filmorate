package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dao.util.FilmSortOption;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
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
    private final DirectorService directorService;

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
        existingFilm.getDirectors().clear();
        existingFilm.getDirectors().addAll(film.getDirectors());
        Film updated = filmRepository.update(existingFilm);
        log.info("Обновлён фильм: id={}, name={}", updated.getId(), updated.getName());

        return updated;
    }

    @Transactional
    public void addLike(int filmId, int userId) {
        checkFilmExists(filmId);
        userService.checkUserExists(userId);

        filmRepository.addLike(filmId, userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    @Transactional
    public void removeLike(int filmId, int userId) {
        checkFilmExists(filmId);
        userService.checkUserExists(userId);

        filmRepository.removeLike(filmId, userId);
        log.info("Пользователь id={} убрал лайк с фильма id={}", userId, filmId);
    }

    public List<Film> popular(int count, Long genreId, Integer year) {
        log.info("Запрос популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);

        List<Film> films;
        films = filmRepository.findPopularByGenreAndYear(count, genreId, year);

        return films;
    }

    public List<Film> getFilmsByDirector(int directorId, FilmSortOption sortBy) {
        Director director = directorService.getDirector(directorId);
        return filmRepository.getFilmsByDirector(director.getId(), sortBy);
    }

    public List<Film> getRecommendations(int userId) {
        userService.checkUserExists(userId);
        return filmRepository.getUserRecommendations(userId);
    }

    public void deleteFilm(int filmId) {
        checkFilmExists(filmId);
        filmRepository.delete(filmId);
        log.info("Удален фильм: id={}", filmId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        userService.getUser(userId);
        userService.getUser(friendId);

        log.info("Поиск общих фильмов для пользователей: userId={}, friendId={}", userId, friendId);
        List<Film> films = filmRepository.getCommonFilms(userId, friendId);
        log.info("Найдено общих фильмов: {}", films.size());
        return films;
    }

    public void checkFilmExists(int filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new ApiException(ErrorCode.FILM_NOT_FOUND, filmId);
        }
    }

    private void validateExistingFilm(Film film) {
        if (film.getMpa() != null) {
            mpaRatingService.getMpaRating(film.getMpa().getId());
        }
        for (Genre genre : film.getGenres()) {
            genreService.getGenre(genre.getId());
        }

        for (Director director : film.getDirectors()) {
            directorService.getDirector(director.getId());
        }
    }
}
