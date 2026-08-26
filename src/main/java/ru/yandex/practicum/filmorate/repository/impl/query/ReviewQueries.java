package ru.yandex.practicum.filmorate.repository.impl.query;

public final class ReviewQueries {

    private ReviewQueries() {
    }

    private static final String SELECT_BASE = """
            SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id,
                   COALESCE(SUM(CASE WHEN rl.is_like IS TRUE THEN 1
                                     WHEN rl.is_like IS FALSE THEN -1
                                     ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_likes rl ON r.id = rl.review_id
            """;

    private static final String GROUP_BY = " GROUP BY r.id ";
    private static final String ORDER_BY_USEFUL = " ORDER BY useful DESC, r.id ";

    public static final String FIND_BY_ID = SELECT_BASE + " WHERE r.id = ? " + GROUP_BY;

    public static final String FIND_ALL = SELECT_BASE + GROUP_BY + ORDER_BY_USEFUL;

    public static final String FIND_ALL_BY_IDS =
            SELECT_BASE + " WHERE r.id IN (%s) " + GROUP_BY + ORDER_BY_USEFUL;

    public static final String FIND_BY_FILM =
            SELECT_BASE + " WHERE (? IS NULL OR r.film_id = ?) " + GROUP_BY
                    + ORDER_BY_USEFUL + " LIMIT ?";

    public static final String INSERT = """
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE id = ?
            """;

    public static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";

    public static final String MERGE_RATING = """
            MERGE INTO review_likes (review_id, user_id, is_like) KEY(review_id, user_id)
            VALUES (?, ?, ?)
            """;

    public static final String DELETE_RATING =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
}
