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
        List<Review> result = jdbc.query(ReviewQueries.FIND_BY_ID, rowMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    public List<Review> findByFilmId(Integer filmId, int count) {
        return findAll(ReviewQueries.FIND_BY_FILM, filmId, filmId, count);
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
    public void delete(Integer id) {
        deleteById(ReviewQueries.DELETE_REVIEW, id);
    }

    @Override
    public Review update(Review review) {
        executeUpdate(ReviewQueries.UPDATE,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        return review;
    }

    public void addRating(int reviewId, int userId, boolean isLike) {
        jdbc.update(ReviewQueries.MERGE_RATING, reviewId, userId, isLike);
    }

    public void removeRating(int reviewId, int userId) {
        jdbc.update(ReviewQueries.DELETE_RATING, reviewId, userId);
    }

    @Override
    public List<Review> getAll() {
        return findAll(ReviewQueries.FIND_ALL);
    }

    @Override
    public List<Review> findAllByIds(Collection<Integer> ids) {
        return findByIds(ReviewQueries.FIND_ALL_BY_IDS, ids);
    }
}
