package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.GenreQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends AbstractRepository<Integer, Genre> {

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> rowMapper) {
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
