package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет HTTP-слой поиска: разбор параметров и форму ответа.
 * Сценарий повторяет коллекцию Postman «Search films by both title and director».
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class FilmSearchControllerTest {

    private static final String NEEDLE = "QwErTy7";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmService filmService;

    @Autowired
    private DirectorService directorService;

    @Test
    void shouldReturnFilmsFoundByTitleAndByDirector() throws Exception {
        Film byTitle = filmService.saveFilm(film("aaa" + NEEDLE + "bbb"));

        Director director = directorService.saveDirector(director("ccc" + NEEDLE + "ddd"));
        Film byDirector = film("совсем другое название");
        byDirector.getDirectors().add(director);
        byDirector = filmService.saveFilm(byDirector);

        mockMvc.perform(get("/films/search")
                        .param("query", NEEDLE)
                        .param("by", "title,director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(byTitle.getId()))
                .andExpect(jsonPath("$[0].directors", hasSize(0)))
                .andExpect(jsonPath("$[1].id").value(byDirector.getId()))
                .andExpect(jsonPath("$[1].directors", hasSize(1)))
                .andExpect(jsonPath("$[1].directors[0].id").value(director.getId()));
    }

    @Test
    void shouldSearchByTitleWhenByParamOmitted() throws Exception {
        filmService.saveFilm(film("aaa" + NEEDLE + "bbb"));

        mockMvc.perform(get("/films/search").param("query", NEEDLE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturn400WhenQueryIsMissing() throws Exception {
        mockMvc.perform(get("/films/search").param("by", "title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenSearchTargetIsUnknown() throws Exception {
        mockMvc.perform(get("/films/search")
                        .param("query", NEEDLE)
                        .param("by", "genre"))
                .andExpect(status().isBadRequest());
    }

    private Film film(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);
        return film;
    }

    private Director director(String name) {
        Director director = new Director();
        director.setName(name);
        return director;
    }
}
