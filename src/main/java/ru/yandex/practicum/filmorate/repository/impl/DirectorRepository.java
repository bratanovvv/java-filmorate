package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.DirectorQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends AbstractRepository<Integer, Director> {

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Director> getById(Integer id) {
        return findOne(DirectorQueries.FIND_BY_ID, id);
    }

    @Override
    public List<Director> getAll() {
        return findAll(DirectorQueries.FIND_ALL);
    }

    @Override
    public List<Director> findAllByIds(Collection<Integer> ids) {
        return findByIds(DirectorQueries.FIND_ALL_BY_IDS, ids);
    }

    @Override
    public Director save(Director entity) {
        int id = insert(DirectorQueries.INSERT, entity.getName());
        entity.setId(id);
        return entity;
    }

    @Override
    public Director update(Director entity) {
        executeUpdate(DirectorQueries.UPDATE, entity.getName(), entity.getId());
        return entity;
    }

    @Override
    public void delete(Integer id) {
        deleteById(DirectorQueries.DELETE, id);
    }
}
