package ru.yandex.practicum.filmorate.repository.impl.query;

public class ReviewQueries {

    private ReviewQueries() {
    }

    public static final String INSERT = """
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?)
            """;

    public static final String FIND_ALL = "SELECT * FROM reviews";
    public static final String FIND_BY_ID = "SELECT * FROM reviews WHERE id = ?";




    // public static final String SELECT_BASE = """
    //            SELECT r.id,
    //                r.content,
    //                r.is_positive,
    //                r.user_id,
    //                r.film_id,
    //                COALESCE(SUM(CASE WHEN rl.is_like THEN 1 ELSE -1 END), 0) AS useful
    //            FROM reviews r
    //            LEFT JOIN review_likes rl ON r.id = rl.review_id
    //            """;
    //
    //    public static final String GROUP_BY = " GROUP BY r.id";
}
