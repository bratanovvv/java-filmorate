package ru.yandex.practicum.filmorate.repository.impl.query;

public final class UserQueries {

    private UserQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM users";
    public static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String FIND_ALL_BY_IDS = "SELECT * FROM users WHERE id IN (%s)";

    public static final String FIND_FRIENDS_BY_USER_IDS = """
            SELECT user_id, friend_id
            FROM friendships
            WHERE user_id IN (%s)
            """;

    public static final String INSERT = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    public static final String DELETE_FRIENDS = "DELETE FROM friendships WHERE user_id = ?";

    public static final String MERGE_FRIEND = """
            MERGE INTO friendships (user_id, friend_id, status_id) KEY(user_id, friend_id)
            VALUES (?, ?, 1)
            """;

    public static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
}