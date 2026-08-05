package ru.yandex.practicum.filmorate.repository.impl.db.query;

public final class FilmQueries {

    private FilmQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM films";
    public static final String FIND_BY_ID = "SELECT * FROM films WHERE id = ?";
    public static final String FIND_ALL_BY_IDS_PREFIX = "SELECT * FROM films WHERE id IN (";
    public static final String FIND_ALL_BY_IDS_SUFFIX = ")";
    public static final String INSERT = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    public static final String DELETE = "DELETE FROM films WHERE id = ?";
    public static final String CLEAR = "DELETE FROM films";

    public static final String INSERT_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
}
