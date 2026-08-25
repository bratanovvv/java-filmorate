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

    public static final String FIND_RECOMMENDATIONS = """
            WITH similar_users AS (
                SELECT l2.user_id, COUNT(*) AS overlap
                FROM likes l1
                JOIN likes l2 ON l1.film_id = l2.film_id
                WHERE l1.user_id = ? AND l2.user_id != ?
                GROUP BY l2.user_id
            ),
            top_similar AS (
                SELECT user_id
                FROM similar_users
                WHERE overlap = (SELECT MAX(overlap) FROM similar_users)
            ),
            candidate_films AS (
                SELECT DISTINCT l.film_id
                FROM likes l
                JOIN top_similar ts ON ts.user_id = l.user_id
                WHERE NOT EXISTS (
                    SELECT 1 FROM likes ul
                    WHERE ul.film_id = l.film_id AND ul.user_id = ?
                )
            ),
            like_counts AS (
                SELECT film_id, COUNT(*) AS cnt
                FROM likes
                GROUP BY film_id
            )
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN candidate_films cf ON cf.film_id = f.id
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN like_counts lc ON lc.film_id = f.id
            ORDER BY COALESCE(lc.cnt, 0) DESC, f.id ASC
            """;
}