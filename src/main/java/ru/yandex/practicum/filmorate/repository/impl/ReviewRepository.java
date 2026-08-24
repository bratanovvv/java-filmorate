package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Review;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;
import ru.yandex.practicum.filmorate.repository.impl.query.ReviewQueries;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends AbstractRepository<Integer, Review> {

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Optional<Review> getById(Integer id) {
        List<Review> reviews = findAll(ReviewQueries.FIND_BY_ID, id);
        if (reviews.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(reviews.getFirst());
    }

    @Override
    public List<Review> getAll() {
        return findAll(ReviewQueries.FIND_ALL);
    }

    @Override
    public List<Review> findAllByIds(Collection<Integer> ids) {
        return List.of();
    }

    @Override
    public Review save(Review review) {
        int id = insert(ReviewQueries.INSERT,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setId(id);
        return review;
    }

    @Override
    public Review update(Review entity) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

}
