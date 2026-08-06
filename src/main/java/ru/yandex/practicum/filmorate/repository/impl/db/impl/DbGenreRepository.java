package ru.yandex.practicum.filmorate.repository.impl.db.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.DbAbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.query.GenreQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
public class DbGenreRepository extends DbAbstractRepository<Integer, Genre> implements GenreRepository {

    public DbGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        return findOne(GenreQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Genre> getAll() {
        return findAll(GenreQueries.FIND_ALL);
    }

    @Override
    public Genre save(Genre genre) {
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        return genre;
    }

    @Override
    public List<Genre> findAllByIds(Collection<Integer> ids) {
        return findByIds(GenreQueries.FIND_ALL_BY_IDS, ids);
    }
}
