package ru.yandex.practicum.filmorate.repository.impl.db.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    private final JdbcTemplate jdbc;

    public FilmRowMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        java.sql.Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_rating_id");
        if (!rs.wasNull()) {
            MpaRating mpa = new MpaRating();
            mpa.setId(mpaId);
            film.setMpa(mpa);
        }

        List<Integer> likes = jdbc.queryForList(
                "SELECT user_id FROM likes WHERE film_id = ?", Integer.class, film.getId());
        film.getLikes().addAll(likes);

        List<Genre> genres = jdbc.query(
                "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?",
                (genreRs, genreRowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(genreRs.getInt("id"));
                    genre.setName(genreRs.getString("name"));
                    return genre;
                },
                film.getId());
        film.getGenres().addAll(genres);

        return film;
    }
}
