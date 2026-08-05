package ru.yandex.practicum.filmorate.repository.impl.db;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.query.FilmQueries;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
public class DbFilmRepository extends DbAbstractRepository<Integer, Film> implements FilmRepository {

    public DbFilmRepository(JdbcTemplate jdbc, RowMapper<Film> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Film> getById(Integer id) {
        return findOne(FilmQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Film> getAll() {
        return findMany(FilmQueries.FIND_ALL);
    }

    @Override
    public void clear() {
        jdbc.execute(FilmQueries.CLEAR);
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
        saveGenres(film);
        return film;
    }

    @Override
    public List<Film> findAllByIds(Collection<Integer> ids) {
        return findAllByIds(FilmQueries.FIND_ALL_BY_IDS_PREFIX, FilmQueries.FIND_ALL_BY_IDS_SUFFIX, ids);
    }

    private void saveGenres(Film film) {
        for (var genre : film.getGenres()) {
            jdbc.update(FilmQueries.INSERT_GENRE, film.getId(), genre.getId());
        }
    }
}
