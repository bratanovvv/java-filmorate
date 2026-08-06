package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRatingRepositoryTest {

    private final MpaRatingRepository mpaRatingRepository;

    @Test
    void shouldFindMpaRatingById() {
        Optional<MpaRating> ratingOptional = mpaRatingRepository.getById(1);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void shouldReturnEmptyOptionalWhenMpaRatingNotFound() {
        Optional<MpaRating> ratingOptional = mpaRatingRepository.getById(999);

        assertThat(ratingOptional).isEmpty();
    }

    @Test
    void shouldReturnAllMpaRatings() {
        List<MpaRating> ratings = mpaRatingRepository.getAll();

        assertThat(ratings).isNotEmpty();
        assertThat(ratings).hasSize(5);
        assertThat(ratings)
                .extracting(MpaRating::getName)
                .contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void shouldFindMpaRatingsByIds() {
        List<MpaRating> found = mpaRatingRepository.findAllByIds(List.of(1, 2, 3));

        assertThat(found)
                .hasSize(3)
                .extracting(MpaRating::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }
}