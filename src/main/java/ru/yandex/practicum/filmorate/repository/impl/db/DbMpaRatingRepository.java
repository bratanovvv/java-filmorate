package ru.yandex.practicum.filmorate.repository.impl.db;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("db")
public class DbMpaRatingRepository extends DbAbstractRepository<Integer, MpaRating> implements MpaRatingRepository {

    private static final String FIND_ALL = "SELECT * FROM mpa_ratings ORDER BY id";
    private static final String FIND_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";
    private static final String FIND_ALL_BY_IDS_PREFIX = "SELECT * FROM mpa_ratings WHERE id IN (";
    private static final String FIND_ALL_BY_IDS_SUFFIX = ")";

    public DbMpaRatingRepository(JdbcTemplate jdbc, RowMapper<MpaRating> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<MpaRating> getById(Integer id) {
        return findOne(FIND_BY_ID, id);
    }

    @Override
    public List<MpaRating> getAll() {
        return findMany(FIND_ALL);
    }

    @Override
    public void clear() {
        jdbc.execute("DELETE FROM mpa_ratings");
    }

    @Override
    public MpaRating save(MpaRating rating) {
        return rating;
    }

    @Override
    public MpaRating update(MpaRating rating) {
        return rating;
    }

    @Override
    public List<MpaRating> findAllByIds(Collection<Integer> ids) {
        return findAllByIds(FIND_ALL_BY_IDS_PREFIX, FIND_ALL_BY_IDS_SUFFIX, ids);
    }
}
