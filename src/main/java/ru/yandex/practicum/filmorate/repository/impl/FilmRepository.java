package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
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
        List<Film> films = findAll(FilmQueries.FIND_BY_ID, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.getFirst();
        loadGenresForFilms(List.of(film));
        loadLikesForFilms(List.of(film));
        return Optional.of(film);
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = findAll(FilmQueries.FIND_ALL);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
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
        jdbc.update(FilmQueries.DELETE_LIKES, film.getId());
        saveGenres(film);
        saveLikes(film);
        return film;
    }

    @Override
    public List<Film> findAllByIds(Collection<Integer> ids) {
        List<Film> films = findByIds(FilmQueries.FIND_ALL_BY_IDS, ids);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    @Override
    public void delete(Integer id) {
        deleteById(FilmQueries.DELETE_FILM, id);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = findAll(FilmQueries.FIND_POPULAR, count);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> films = findAll(FilmQueries.COMMON_FILMS, userId, friendId);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
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

    private void saveLikes(Film film) {
        saveRelation(film.getId(), film.getLikes(), FilmQueries.MERGE_LIKE);
    }
}
