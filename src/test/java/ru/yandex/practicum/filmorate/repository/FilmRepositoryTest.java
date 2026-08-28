package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.entity.dao.User;
import ru.yandex.practicum.filmorate.repository.impl.DirectorRepository;
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
    private final DirectorRepository directorRepository;

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

        filmRepository.addLike(film.getId(), user.getId());

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

    // -------- RECOMMENDATIONS --------

    @Test
    void shouldReturnRecommendationsFromSimilarUser() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        User user3 = userRepository.save(validUser());

        like(film1, user1);
        like(film2, user1);
        like(film1, user2);
        like(film2, user2);
        like(film3, user2);
        like(film1, user3);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film3.getId());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoLikes() {
        Film film1 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        like(film1, user2);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoOverlapWithOtherUsers() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        like(film1, user1);
        like(film2, user2);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations).isEmpty();
    }

    @Test
    void shouldExcludeFilmsAlreadyLikedByTargetUser() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        like(film1, user1);
        like(film2, user1);
        like(film3, user1);
        like(film1, user2);
        like(film2, user2);
        like(film3, user2);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations).isEmpty();
    }

    @Test
    void shouldSelectOnlyTopSimilarUserFilms() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());
        Film film4 = filmRepository.save(validFilm());
        Film film5 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        User user3 = userRepository.save(validUser());

        like(film1, user1);
        like(film2, user1);
        like(film1, user2);
        like(film2, user2);
        like(film3, user2);
        like(film4, user2);
        like(film1, user3);
        like(film5, user3);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(film3.getId(), film4.getId())
                .doesNotContain(film5.getId());
    }

    @Test
    void shouldCombineRecommendationsFromTiedSimilarUsers() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());
        Film film4 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        User user3 = userRepository.save(validUser());

        like(film1, user1);
        like(film2, user1);
        like(film1, user2);
        like(film2, user2);
        like(film3, user2);
        like(film1, user3);
        like(film2, user3);
        like(film4, user3);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(film3.getId(), film4.getId());
    }

    @Test
    void shouldOrderRecommendationsByPopularity() {
        Film film1 = filmRepository.save(validFilm());
        Film film2 = filmRepository.save(validFilm());
        Film film3 = filmRepository.save(validFilm());
        User user1 = userRepository.save(validUser());
        User user2 = userRepository.save(validUser());
        User user3 = userRepository.save(validUser());

        like(film1, user1);
        like(film1, user2);
        like(film2, user2);
        like(film3, user2);
        like(film3, user3);

        List<Film> recommendations = filmRepository.getUserRecommendations(user1.getId());

        assertThat(recommendations)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(film3.getId(), film2.getId());
    }

    // -------- SEARCH --------

    @Test
    void shouldSearchTitleIgnoringCase() {
        Film target = filmRepository.save(namedFilm("Крадущийся тигр"));

        assertThat(filmRepository.searchFilms("%КРАД%", null))
                .extracting(Film::getId)
                .containsExactly(target.getId());
    }

    @Test
    void shouldNotMixSearchBranches() {
        Director director = directorRepository.save(namedDirector("Тарантино"));
        Film byDirector = filmRepository.save(filmWithDirectors(namedFilm("Убить Билла"), director));
        Film byTitle = filmRepository.save(namedFilm("Фильм про Тарантино"));

        assertThat(filmRepository.searchFilms("%таранти%", null))
                .extracting(Film::getId)
                .containsExactly(byTitle.getId());

        assertThat(filmRepository.searchFilms(null, "%таранти%"))
                .extracting(Film::getId)
                .containsExactly(byDirector.getId());
    }

    @Test
    void shouldReturnFilmOnceWhenSeveralDirectorsMatch() {
        Director first = directorRepository.save(namedDirector("Тарантино Квентин"));
        Director second = directorRepository.save(namedDirector("Тарантино Роберт"));
        Film target = filmRepository.save(
                filmWithDirectors(namedFilm("Общий фильм"), first, second));

        assertThat(filmRepository.searchFilms(null, "%таранти%"))
                .extracting(Film::getId)
                .containsExactly(target.getId());
    }

    @Test
    void shouldReturnFilmOnceWhenTitleAndDirectorBothMatch() {
        Director director = directorRepository.save(namedDirector("Тарантино"));
        Film target = filmRepository.save(
                filmWithDirectors(namedFilm("Тарантино снимает"), director));

        assertThat(filmRepository.searchFilms("%таранти%", "%таранти%"))
                .extracting(Film::getId)
                .containsExactly(target.getId());
    }

    @Test
    void shouldOrderSearchResultsByLikesThenById() {
        Film first = filmRepository.save(namedFilm("Поиск один"));
        Film second = filmRepository.save(namedFilm("Поиск два"));
        Film popular = filmRepository.save(namedFilm("Поиск три"));

        User user = userRepository.save(validUser());
        like(popular, user);

        assertThat(filmRepository.searchFilms("%поиск%", null))
                .extracting(Film::getId)
                .containsExactly(popular.getId(), first.getId(), second.getId());
    }

    @Test
    void shouldLoadGenresAndDirectorsForFoundFilms() {
        Director director = directorRepository.save(namedDirector("Тарантино"));
        Film withDirector = filmRepository.save(
                filmWithDirectors(namedFilm("Поиск с режиссёром"), director));
        Film withoutDirector = filmRepository.save(namedFilm("Поиск без режиссёра"));

        List<Film> found = filmRepository.searchFilms("%поиск%", null);

        assertThat(found).hasSize(2);
        assertThat(found)
                .filteredOn(f -> f.getId().equals(withDirector.getId()))
                .singleElement()
                .satisfies(f -> assertThat(f.getDirectors()).hasSize(1));
        assertThat(found)
                .filteredOn(f -> f.getId().equals(withoutDirector.getId()))
                .singleElement()
                .satisfies(f -> assertThat(f.getDirectors()).isEmpty());
    }

    @Test
    void shouldNotFailOnLikeSpecialCharactersInQuery() {
        filmRepository.save(namedFilm("Матрица"));

        assertThat(filmRepository.searchFilms("%100%%", null)).isEmpty();
        assertThat(filmRepository.searchFilms("%a\\b%", null)).isEmpty();
    }

    private void like(Film film, User user) {
        filmRepository.addLike(film.getId(), user.getId());
    }

    private Film namedFilm(String name) {
        Film film = validFilm();
        film.setName(name);
        return film;
    }

    private Film filmWithDirectors(Film film, Director... directors) {
        film.getDirectors().addAll(List.of(directors));
        return film;
    }

    private Director namedDirector(String name) {
        Director director = new Director();
        director.setName(name);
        return director;
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