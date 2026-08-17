package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;
import ru.yandex.practicum.filmorate.repository.impl.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmRepositoryTest {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Test
    void shouldSaveAndFindFilmById() {
        Film film = validFilm();

        Film saved = filmRepository.save(film);

        Optional<Film> filmOptional = filmRepository.getById(saved.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", saved.getId());
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Matrix");
                    assertThat(f).hasFieldOrPropertyWithValue("description", "Description");
                    assertThat(f).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(1999, 3, 31));
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 136);
                });
    }

    @Test
    void shouldReturnEmptyOptionalWhenFilmNotFound() {
        Optional<Film> filmOptional = filmRepository.getById(999);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    void shouldReturnAllFilms() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());

        List<Film> films = filmRepository.getAll();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .contains(film1.getId(), film2.getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film saved = filmRepository.save(validFilm());

        saved.setName("New name");
        saved.setDescription("New description");
        saved.setReleaseDate(LocalDate.of(2020, 1, 1));
        saved.setDuration(120);

        filmRepository.update(saved);

        Optional<Film> updated = filmRepository.getById(saved.getId());

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("name", "New name");
                    assertThat(f).hasFieldOrPropertyWithValue("description", "New description");
                    assertThat(f).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2020, 1, 1));
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 120);
                });
    }

    @Test
    void shouldFindFilmsByIds() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());

        List<Film> found = filmRepository.findAllByIds(
                List.of(film1.getId(), film2.getId()));

        assertThat(found)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(film1.getId(), film2.getId());
    }

    @Test
    void shouldReturnPopularFilmsOrderedByLikes() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());

        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());

        film1.getLikes().add(user1.getId());
        film1.getLikes().add(user2.getId());
        filmRepository.update(film1);

        film2.getLikes().add(user1.getId());
        filmRepository.update(film2);

        List<Film> popular = filmRepository.getPopularFilms(2);

        assertThat(popular)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId());
    }

    @Test
    void shouldReturnAllFilmsWhenCountExceedsTotal() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());

        List<Film> popular = filmRepository.getPopularFilms(100);

        assertThat(popular)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(film1.getId(), film2.getId());
    }

    @Test
    void shouldPersistAndLoadLikes() {
        Film film = filmRepository.save(validFilm());
        User user = userRepository.save(validUser());

        film.getLikes().add(user.getId());
        filmRepository.update(film);

        Optional<Film> loaded = filmRepository.getById(film.getId());

        assertThat(loaded)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getLikes()).contains(user.getId()));
    }

    @Test
    void shouldDeleteFilm() {
        Film saved = filmRepository.save(validFilm());

        filmRepository.delete(saved.getId());

        Optional<Film> loaded = filmRepository.getById(saved.getId());
        assertThat(loaded).isEmpty();
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);
        return film;
    }

    private User validUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}