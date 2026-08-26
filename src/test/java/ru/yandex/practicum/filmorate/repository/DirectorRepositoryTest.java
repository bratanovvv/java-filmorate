package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.repository.impl.DirectorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorRepositoryTest {

    private final DirectorRepository directorRepository;

    @Test
    void shouldSaveAndFindDirectorById() {
        Director director = validDirector();

        Director saved = directorRepository.save(director);

        Optional<Director> directorOptional = directorRepository.getById(saved.getId());

        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(d -> {
                    assertThat(d).hasFieldOrPropertyWithValue("id", saved.getId());
                    assertThat(d).hasFieldOrPropertyWithValue("name", "Christopher Nolan");
                });
    }

    @Test
    void shouldReturnEmptyOptionalWhenDirectorNotFound() {
        Optional<Director> directorOptional = directorRepository.getById(999);

        assertThat(directorOptional).isEmpty();
    }

    @Test
    void shouldReturnAllDirectors() {
        Director director1 = directorRepository.save(validDirector());
        Director director2 = directorRepository.save(validDirector());

        List<Director> directors = directorRepository.getAll();

        assertThat(directors)
                .hasSize(2)
                .extracting(Director::getId)
                .contains(director1.getId(), director2.getId());
    }

    @Test
    void shouldUpdateDirector() {
        Director saved = directorRepository.save(validDirector());

        saved.setName("Steven Spielberg");

        directorRepository.update(saved);

        Optional<Director> updated = directorRepository.getById(saved.getId());

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(d -> {
                    assertThat(d).hasFieldOrPropertyWithValue("name", "Steven Spielberg");
                });
    }

    @Test
    void shouldFindDirectorsByIds() {
        Director director1 = directorRepository.save(validDirector());
        Director director2 = directorRepository.save(validDirector());

        List<Director> found = directorRepository.findAllByIds(
                List.of(director1.getId(), director2.getId()));

        assertThat(found)
                .hasSize(2)
                .extracting(Director::getId)
                .containsExactlyInAnyOrder(director1.getId(), director2.getId());
    }

    @Test
    void shouldDeleteDirector() {
        Director saved = directorRepository.save(validDirector());

        directorRepository.delete(saved.getId());

        Optional<Director> loaded = directorRepository.getById(saved.getId());
        assertThat(loaded).isEmpty();
    }

    private Director validDirector() {
        Director director = new Director();
        director.setName("Christopher Nolan");
        return director;
    }
}
