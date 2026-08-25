package ru.yandex.practicum.filmorate.repository.impl.query;

public final class FilmQueries {

    private FilmQueries() {
    }

    public static final String FIND_ALL = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            """;

    public static final String FIND_BY_ID = FIND_ALL + " WHERE f.id = ?";

    public static final String FIND_POPULAR = FIND_ALL + """
             ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC, f.id ASC
             LIMIT ?
            """;

    public static final String FIND_ALL_BY_IDS = FIND_ALL + " WHERE f.id IN (%s)";

    public static final String FIND_GENRES_BY_FILM_IDS = """
            SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id IN (%s)
            """;

    public static final String FIND_LIKES_BY_FILM_IDS = """
            SELECT film_id, user_id
            FROM likes
            WHERE film_id IN (%s)
            """;

    public static final String INSERT = """
            INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
            WHERE id = ?
            """;

    public static final String DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    public static final String DELETE_LIKES = "DELETE FROM likes WHERE film_id = ?";

    public static final String MERGE_GENRE = """
            MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id)
            VALUES (?, ?)
            """;

    public static final String MERGE_LIKE = """
            MERGE INTO likes (film_id, user_id) KEY(film_id, user_id)
            VALUES (?, ?)
            """;

    public static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    public static final String COMMON_FILMS = """
    SELECT
        f.id,
        f.name,
        f.description,
        f.release_date,
        f.duration,
        f.mpa_rating_id,
        m.name AS mpa_name
    FROM films f
    LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
    WHERE f.id IN (
            SELECT l1.film_id
                    FROM likes l1
                    WHERE l1.user_id = ?
    )
    AND f.id IN (
            SELECT l2.film_id
                    FROM likes l2
                    WHERE l2.user_id = ?
    )
    ORDER BY f.id;
    """;
}