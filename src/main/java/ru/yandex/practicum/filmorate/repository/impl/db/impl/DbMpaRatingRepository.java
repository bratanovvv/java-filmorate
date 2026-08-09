package ru.yandex.practicum.filmorate.repository.impl.db.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.DbAbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.db.query.MpaRatingQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
public class DbMpaRatingRepository extends DbAbstractRepository<Integer, MpaRating> implements MpaRatingRepository {

    public DbMpaRatingRepository(JdbcTemplate jdbc, RowMapper<MpaRating> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<MpaRating> getById(Integer id) {
        return findOne(MpaRatingQueries.FIND_BY_ID, id);
    }

    @Override
    public List<MpaRating> getAll() {
        return findAll(MpaRatingQueries.FIND_ALL);
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
        return findByIds(MpaRatingQueries.FIND_ALL_BY_IDS, ids);
    }
}
