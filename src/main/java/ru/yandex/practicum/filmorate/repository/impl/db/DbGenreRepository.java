package ru.yandex.practicum.filmorate.repository.impl.db;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("db")
public class DbGenreRepository extends DbAbstractRepository<Integer, Genre> implements GenreRepository {

    private static final String FIND_ALL = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_BY_IDS_PREFIX = "SELECT * FROM genres WHERE id IN (";
    private static final String FIND_ALL_BY_IDS_SUFFIX = ")";

    public DbGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        return findOne(FIND_BY_ID, id);
    }

    @Override
    public List<Genre> getAll() {
        return findMany(FIND_ALL);
    }

    @Override
    public void clear() {
        jdbc.execute("DELETE FROM genres");
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
        return findAllByIds(FIND_ALL_BY_IDS_PREFIX, FIND_ALL_BY_IDS_SUFFIX, ids);
    }
}
