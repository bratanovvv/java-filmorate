package ru.yandex.practicum.filmorate.repository.impl.query;

public final class FilmQueries {

    private FilmQueries() {
    }

    public static final String EXISTS_BY_ID = "SELECT 1 FROM films WHERE id = ?";

    public static final String FIND_ALL = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            """;

    public static final String FIND_BY_ID = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            WHERE f.id = ?
            """;

    public static final String FIND_POPULAR = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC, f.id ASC
            LIMIT ?
            """;

    public static final String FIND_ALL_BY_IDS = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            WHERE f.id IN (%s)
            """;

    public static final String FIND_GENRES_BY_FILM_IDS = """
            SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id IN (%s)
            """;

    public static final String FIND_DIRECTORS_BY_FILM_IDS = """
            SELECT fd.film_id, d.id AS director_id, d.name AS director_name
            FROM film_directors fd
            JOIN directors d ON fd.director_id = d.id
            WHERE fd.film_id IN (%s)
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

    public static final String DELETE = """
            DELETE FROM films WHERE id = ?
            """;

    public static final String DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";

    public static final String DELETE_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";

    public static final String MERGE_GENRE = """
            MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id)
            VALUES (?, ?)
            """;
    public static final String MERGE_DIRECTOR = """
            MERGE INTO film_directors (film_id, director_id) KEY(film_id, director_id)
            VALUES (?, ?)
            """;

    public static final String ADD_LIKE = """
            MERGE INTO likes (film_id, user_id) KEY(film_id, user_id)
            VALUES (?, ?)
            """;

    public static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public static final String FIND_BY_DIRECTOR_ORDER_BY_YEAR = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN film_directors fd ON f.id = fd.film_id
            WHERE fd.director_id = ?
            ORDER BY f.release_date ASC, f.id ASC
            """;

    public static final String FIND_BY_DIRECTOR_ORDER_BY_LIKES = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            LEFT JOIN film_directors fd ON f.id = fd.film_id
            WHERE fd.director_id = ?
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC, f.id ASC
            """;

    public static final String SEARCH = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
            WHERE LOWER(f.name) LIKE LOWER(?)
               OR EXISTS (
                   SELECT 1
                   FROM film_directors fd
                   JOIN directors d ON d.id = fd.director_id
                   WHERE fd.film_id = f.id
                     AND LOWER(d.name) LIKE LOWER(?)
               )
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC, f.id ASC
            """;

    public static final String FIND_POPULAR_BY_GENRE_AND_YEAR = """
        SELECT
            f.id,
            f.name,
            f.description,
            f.release_date,
            f.duration,
            f.mpa_rating_id,
            m.name AS mpa_name,
            COUNT(l.user_id) AS likes_count
        FROM films f
        LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
        LEFT JOIN likes l ON f.id = l.film_id
        WHERE (? IS NULL
            OR EXISTS (
                SELECT 1
                FROM film_genres fg
                WHERE fg.film_id = f.id
                AND fg.genre_id = ?
            ))
            AND (? IS NULL
                OR EXTRACT(YEAR FROM f.release_date) = ?)
        GROUP BY f.id, m.name
        ORDER BY likes_count DESC, f.id
        LIMIT ?
        """;

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