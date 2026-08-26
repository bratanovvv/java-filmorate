package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    // -------- CREATE --------

    @Test
    void shouldSaveFilm() {
        Film film = validFilm();

        Film saved = filmService.saveFilm(film);

        assertNotNull(saved.getId());
        assertEquals("Matrix", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(LocalDate.of(1999, 3, 31), saved.getReleaseDate());
        assertEquals(136, saved.getDuration());
    }

    // -------- GET --------

    @Test
    void shouldFindSavedFilm() {
        Film saved = filmService.saveFilm(validFilm());

        Film found = filmService.getFilm(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getName(), found.getName());
        assertEquals(saved.getDescription(), found.getDescription());
        assertEquals(saved.getReleaseDate(), found.getReleaseDate());
        assertEquals(saved.getDuration(), found.getDuration());
    }

    @Test
    void shouldThrowWhenFilmNotFound() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getFilm(999)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- UPDATE --------

    @Test
    void shouldUpdateFilm() {
        Film saved = filmService.saveFilm(validFilm());

        Film update = validFilm();
        update.setId(saved.getId());
        update.setName("New name");
        update.setDescription("New description");
        update.setReleaseDate(LocalDate.of(2020, 1, 1));
        update.setDuration(120);

        Film updated = filmService.updateFilm(update);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("New name", updated.getName());
        assertEquals("New description", updated.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), updated.getReleaseDate());
        assertEquals(120, updated.getDuration());
    }

    @Test
    void shouldThrowWhenUpdatingUnknownFilm() {
        Film film = validFilm();
        film.setId(999);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.updateFilm(film)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- LIST --------

    @Test
    void shouldReturnEmptyList() {
        List<Film> films = filmService.getFilms();

        assertTrue(films.isEmpty());
    }

    @Test
    void shouldReturnAllFilms() {
        filmService.saveFilm(validFilm());
        filmService.saveFilm(validFilm());

        List<Film> films = filmService.getFilms();

        assertEquals(2, films.size());
    }

    // -------- LIKES --------

    @Test
    void shouldAddLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());

        filmService.addLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertEquals(1, likedFilm.getLikes().size());
        assertTrue(likedFilm.getLikes().contains(user.getId()));
    }

    @Test
    void shouldRemoveLike() {
        Film film = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());
        filmService.addLike(film.getId(), user.getId());

        filmService.removeLike(film.getId(), user.getId());

        Film likedFilm = filmService.getFilm(film.getId());
        assertTrue(likedFilm.getLikes().isEmpty());
    }

    @Test
    void shouldThrowWhenLikeFilmNotFound() {
        User user = userService.saveUser(validUser());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.addLike(999, user.getId())
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenLikeUserNotFound() {
        Film film = filmService.saveFilm(validFilm());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.addLike(film.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenRemoveLikeUserNotFound() {
        Film film = filmService.saveFilm(validFilm());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.removeLike(film.getId(), 999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldReturnPopularFilms() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> popular = filmService.popular(2, null, null);

        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }

    @Test
    void shouldReturnAllFilmsWhenCountExceedsTotal() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        User user = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user.getId());

        List<Film> popular = filmService.popular(100, null, null);

        assertEquals(2, popular.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoFilms() {
        List<Film> popular = filmService.popular(10, null, null);

        assertTrue(popular.isEmpty());
    }

    @Test
    void shouldReturnPopularFilmsByGenre() {
        // Создаём фильмы с разными жанрами
        Film film1 = filmService.saveFilm(validFilmWithGenre(1)); // Комедия
        Film film2 = filmService.saveFilm(validFilmWithGenre(1)); // Комедия
        Film film3 = filmService.saveFilm(validFilmWithGenre(2)); // Драма

        // Добавляем лайки
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        // film1 - 2 лайка (самый популярный в жанре Комедия)
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        // film2 - 1 лайк
        filmService.addLike(film2.getId(), user1.getId());

        // film3 - 3 лайка (самый популярный в жанре Драма, но не в Комедии)
        filmService.addLike(film3.getId(), user1.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film3.getId(), user3.getId());

        // Получаем популярные фильмы только жанра Комедия (genreId = 1)
        List<Film> popular = filmService.popular(10, 1L, null);

        // Проверяем: должны быть только фильмы с жанром Комедия
        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId()); // film1 имеет 2 лайка
        assertEquals(film2.getId(), popular.get(1).getId()); // film2 имеет 1 лайк
    }

    @Test
    void shouldReturnPopularFilmsByYear() {
        // Создаём фильмы разных лет
        Film film2000 = filmService.saveFilm(validFilmWithYear(2000));
        Film film2001 = filmService.saveFilm(validFilmWithYear(2001));
        Film film2002 = filmService.saveFilm(validFilmWithYear(2002));

        // Добавляем лайки
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());

        // film2001 - самый популярный в 2001 году
        filmService.addLike(film2001.getId(), user1.getId());
        filmService.addLike(film2001.getId(), user2.getId());

        // film2002 - 1 лайк
        filmService.addLike(film2002.getId(), user1.getId());

        // film2000 - 1 лайк
        filmService.addLike(film2000.getId(), user2.getId());

        // Получаем популярные фильмы за 2001 год
        List<Film> popular = filmService.popular(10, null, 2001);

        // Проверяем: должен быть только один фильм за 2001 год
        assertEquals(1, popular.size());
        assertEquals(film2001.getId(), popular.get(0).getId());
    }

    @Test
    void shouldReturnPopularFilmsByGenreAndYear() {
        // Создаём фильмы с разными жанрами и годами
        Film film1 = filmService.saveFilm(validFilmWithGenreAndYear(1, 2001)); // Комедия, 2001
        Film film2 = filmService.saveFilm(validFilmWithGenreAndYear(1, 2001)); // Комедия, 2001
        Film film3 = filmService.saveFilm(validFilmWithGenreAndYear(1, 2002)); // Комедия, 2002
        Film film4 = filmService.saveFilm(validFilmWithGenreAndYear(2, 2001)); // Драма, 2001

        // Добавляем лайки
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        // film1 - 3 лайка (самый популярный в жанре Комедия за 2001)
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        // film2 - 2 лайка
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());

        // film3 - 1 лайк
        filmService.addLike(film3.getId(), user1.getId());

        // film4 - 4 лайка (самый популярный, но не подходит по жанру)
        filmService.addLike(film4.getId(), user1.getId());
        filmService.addLike(film4.getId(), user2.getId());
        filmService.addLike(film4.getId(), user3.getId());
        filmService.addLike(film4.getId(), user1.getId()); // дублируем, чтобы было больше

        // Получаем популярные фильмы: Комедия (genreId=1) за 2001 год
        List<Film> popular = filmService.popular(10, 1L, 2001);

        // Проверяем: должны быть только фильмы жанра Комедия за 2001
        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId()); // 3 лайка
        assertEquals(film2.getId(), popular.get(1).getId()); // 2 лайка
    }

    @Test
    void shouldReturnPopularFilmsByGenreWithLimit() {
        // Создаём 5 фильмов жанра Комедия
        for (int i = 0; i < 5; i++) {
            Film film = filmService.saveFilm(validFilmWithGenre(1));
            User user = userService.saveUser(validUser());
            filmService.addLike(film.getId(), user.getId());
        }

        // Запрашиваем только топ-3
        List<Film> popular = filmService.popular(3, 1L, null);

        // Проверяем: должно быть ровно 3 фильма
        assertEquals(3, popular.size());
    }

    @Test
    void shouldReturnEmptyListWhenGenreNotFound() {
        // Создаём фильмы с жанром Комедия (1)
        filmService.saveFilm(validFilmWithGenre(1));
        filmService.saveFilm(validFilmWithGenre(1));

        // Запрашиваем несуществующий жанр (999)
        List<Film> popular = filmService.popular(10, 999L, null);

        // Должен быть пустой список
        assertTrue(popular.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenYearNotFound() {
        // Создаём фильмы 2000 года
        filmService.saveFilm(validFilmWithYear(2000));
        filmService.saveFilm(validFilmWithYear(2000));

        // Запрашиваем несуществующий год (1999)
        List<Film> popular = filmService.popular(10, null, 1999);

        // Должен быть пустой список
        assertTrue(popular.isEmpty());
    }

    @Test
    void shouldReturnPopularFilmsByYearWithMultipleFilms() {
        // Создаём 3 фильма 2020 года с разным количеством лайков
        Film film1 = filmService.saveFilm(validFilmWithYear(2020));
        Film film2 = filmService.saveFilm(validFilmWithYear(2020));
        Film film3 = filmService.saveFilm(validFilmWithYear(2020));

        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        // film1 - 3 лайка
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        // film2 - 2 лайка
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());

        // film3 - 1 лайк
        filmService.addLike(film3.getId(), user1.getId());

        List<Film> popular = filmService.popular(10, null, 2020);

        // Проверяем порядок сортировки по популярности
        assertEquals(3, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId()); // 3 лайка
        assertEquals(film2.getId(), popular.get(1).getId()); // 2 лайка
        assertEquals(film3.getId(), popular.get(2).getId()); // 1 лайк
    }

    // -------- RECOMMENDATIONS --------

    @Test
    void shouldReturnRecommendationsFromSimilarUser() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(1, recommendations.size());
        assertEquals(film3.getId(), recommendations.get(0).getId());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoLikes() {
        Film film1 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoOverlapWithOtherUsers() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldExcludeFilmsAlreadyLikedByTargetUser() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film3.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldSelectOnlyTopSimilarUserFilms() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        Film film4 = filmService.saveFilm(validFilm());
        Film film5 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film4.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());
        filmService.addLike(film5.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertTrue(recommendations.stream().map(Film::getId).toList()
                .containsAll(List.of(film3.getId(), film4.getId())));
        assertFalse(recommendations.stream().map(Film::getId).toList().contains(film5.getId()));
    }

    @Test
    void shouldCombineRecommendationsFromTiedSimilarUsers() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        Film film4 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());
        filmService.addLike(film2.getId(), user3.getId());
        filmService.addLike(film4.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertTrue(recommendations.stream().map(Film::getId).toList()
                .containsAll(List.of(film3.getId(), film4.getId())));
    }

    @Test
    void shouldOrderRecommendationsByPopularity() {
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film3.getId(), user2.getId());
        filmService.addLike(film3.getId(), user3.getId());

        List<Film> recommendations = filmService.getRecommendations(user1.getId());

        assertEquals(2, recommendations.size());
        assertEquals(film3.getId(), recommendations.get(0).getId());
        assertEquals(film2.getId(), recommendations.get(1).getId());
    }

    @Test
    void shouldThrowWhenRecommendationsForUnknownUser() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getRecommendations(999)
        );
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
    }

    // -------- DELETE --------

    @Test
    void shouldDeleteFilm() {
        Film saved = filmService.saveFilm(validFilm());

        filmService.deleteFilm(saved.getId());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.getFilm(saved.getId())
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    @Test
    void shouldThrowWhenDeleteUnknownFilm() {
        ApiException ex = assertThrows(
                ApiException.class,
                () -> filmService.deleteFilm(999)
        );
        assertEquals(ErrorCode.FILM_NOT_FOUND, ex.getCode());
    }

    // -------- helper --------

    private Film validFilmWithGenre(int genreId) {
        Film film = validFilm();
        Genre genre = new Genre();
        genre.setId(genreId);
        film.getGenres().add(genre);
        return film;
    }

    private Film validFilmWithYear(int year) {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(year, 6, 15));
        return film;
    }

    private Film validFilmWithGenreAndYear(int genreId, int year) {
        Film film = validFilmWithYear(year);
        Genre genre = new Genre();
        genre.setId(genreId);
        film.getGenres().add(genre);
        return film;
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
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

    @Test
    void shouldReturnCommonFilms() {
        // Создаём пользователей
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());
        User user3 = userService.saveUser(validUser());

        // Создаём фильмы
        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());
        Film film3 = filmService.saveFilm(validFilm());
        Film film4 = filmService.saveFilm(validFilm());

        // Добавляем лайки
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());  // Общий

        filmService.addLike(film2.getId(), user1.getId());  // Только user1
        filmService.addLike(film2.getId(), user3.getId());

        filmService.addLike(film3.getId(), user2.getId());  // Только user2
        filmService.addLike(film3.getId(), user3.getId());

        filmService.addLike(film4.getId(), user1.getId());
        filmService.addLike(film4.getId(), user2.getId());  // Общий
        filmService.addLike(film4.getId(), user3.getId());

        // Общие фильмы user1 и user2 → film1 и film4
        List<Film> common = filmService.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(2, common.size());
        List<Integer> commonIds = common.stream().map(Film::getId).toList();
        assertTrue(commonIds.contains(film1.getId()));
        assertTrue(commonIds.contains(film4.getId()));
        assertFalse(commonIds.contains(film2.getId()));
        assertFalse(commonIds.contains(film3.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFilms() {
        User user1 = userService.saveUser(validUser());
        User user2 = userService.saveUser(validUser());

        Film film1 = filmService.saveFilm(validFilm());
        Film film2 = filmService.saveFilm(validFilm());

        filmService.addLike(film1.getId(), user1.getId());  // Только user1
        filmService.addLike(film2.getId(), user2.getId());  // Только user2

        List<Film> common = filmService.getCommonFilms(user1.getId(), user2.getId());

        assertTrue(common.isEmpty());
    }
}