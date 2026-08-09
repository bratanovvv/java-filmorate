package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepositoryTest {

    private final GenreRepository genreRepository;

    @Test
    void shouldFindGenreById() {
        Optional<Genre> genreOptional = genreRepository.getById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void shouldReturnEmptyOptionalWhenGenreNotFound() {
        Optional<Genre> genreOptional = genreRepository.getById(999);

        assertThat(genreOptional).isEmpty();
    }

    @Test
    void shouldReturnAllGenres() {
        List<Genre> genres = genreRepository.getAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres)
                .extracting(Genre::getName)
                .contains("Комедия", "Драма", "Мультфильм",
                        "Триллер", "Документальный", "Боевик");
    }

    @Test
    void shouldFindGenresByIds() {
        List<Genre> found = genreRepository.findAllByIds(List.of(1, 2, 3));

        assertThat(found)
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }
}