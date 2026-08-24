package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dao.util.FilmSortOption;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.FilmQueries;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends AbstractRepository<Integer, Film> {

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Film> getById(Integer id) {
        Optional<Film> filmOpt = findOne(FilmQueries.FIND_BY_ID, id);
        filmOpt.ifPresent(f -> loadFilmsLinkedEntities(List.of(f)));
        return filmOpt;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = findAll(FilmQueries.FIND_ALL);
        loadFilmsLinkedEntities(films);
        return films;
    }

    @Override
    public Film save(Film film) {
        int id = insert(FilmQueries.INSERT,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null);
        film.setId(id);
        saveGenres(film);
        saveDirectors(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        executeUpdate(FilmQueries.UPDATE,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        jdbc.update(FilmQueries.DELETE_GENRES, film.getId());
        jdbc.update(FilmQueries.DELETE_DIRECTORS, film.getId());
        saveGenres(film);
        saveDirectors(film);
        return film;
    }

    @Override
    public List<Film> findAllByIds(Collection<Integer> ids) {
        List<Film> films = findByIds(FilmQueries.FIND_ALL_BY_IDS, ids);
        loadFilmsLinkedEntities(films);
        return films;
    }

    @Override
    public void delete(Integer id) {
        deleteById(FilmQueries.DELETE, id);
    }

    public boolean existsById(Integer id) {
        return exists(FilmQueries.EXISTS_BY_ID, id);
    }

    public void addLike(Integer filmId, Integer userId) {
        executeUpdate(FilmQueries.ADD_LIKE, filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        deleteById(FilmQueries.DELETE_LIKE, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = findAll(FilmQueries.FIND_POPULAR, count);
        loadFilmsLinkedEntities(films);
        return films;
    }

    public List<Film> getFilmsByDirector(Integer directorId, FilmSortOption sortBy) {
        List<Film> films = switch (sortBy) {
            case year -> findAll(FilmQueries.FIND_BY_DIRECTOR_ORDER_BY_YEAR, directorId);
            case likes -> findAll(FilmQueries.FIND_BY_DIRECTOR_ORDER_BY_LIKES, directorId);
        };
        loadFilmsLinkedEntities(films);
        return films;
    }

    public void loadFilmsLinkedEntities(List<Film> films) {
        loadGenresForFilms(films);
        loadDirectorsForFilms(films);
        loadLikesForFilms(films);
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        String placeholders = filmIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = FilmQueries.FIND_GENRES_BY_FILM_IDS.formatted(placeholders);

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));

        jdbc.query(query, rs -> {
            int filmId = rs.getInt("film_id");
            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre_name");
            Film film = filmMap.get(filmId);
            if (film != null) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(genreName);
                film.getGenres().add(genre);
            }
        }, filmIds.toArray());
    }

    private void loadDirectorsForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        String placeholders = filmIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = FilmQueries.FIND_DIRECTORS_BY_FILM_IDS.formatted(placeholders);

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));

        jdbc.query(query, rs -> {
            int filmId = rs.getInt("film_id");
            int directorId = rs.getInt("director_id");
            String directorName = rs.getString("director_name");
            Film film = filmMap.get(filmId);
            if (film != null) {
                Director director = new Director();
                director.setId(directorId);
                director.setName(directorName);
                film.getDirectors().add(director);
            }
        }, filmIds.toArray());
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        String placeholders = filmIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = FilmQueries.FIND_LIKES_BY_FILM_IDS.formatted(placeholders);

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));

        jdbc.query(query, rs -> {
            int filmId = rs.getInt("film_id");
            int userId = rs.getInt("user_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                film.getLikes().add(userId);
            }
        }, filmIds.toArray());
    }

    private void saveGenres(Film film) {
        saveRelation(film.getId(), film.getGenres().stream().map(Genre::getId).toList(), FilmQueries.MERGE_GENRE);
    }

    private void saveDirectors(Film film) {
        saveRelation(film.getId(), film.getDirectors().stream().map(Director::getId).toList(), FilmQueries.MERGE_DIRECTOR);
    }
}
