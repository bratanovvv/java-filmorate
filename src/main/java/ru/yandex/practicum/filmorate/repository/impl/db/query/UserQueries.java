package ru.yandex.practicum.filmorate.repository.impl.db.query;

public final class UserQueries {

    private UserQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM users";
    public static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String FIND_ALL_BY_IDS_PREFIX = "SELECT * FROM users WHERE id IN (";
    public static final String FIND_ALL_BY_IDS_SUFFIX = ")";
    public static final String INSERT = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    public static final String DELETE = "DELETE FROM users WHERE id = ?";
    public static final String CLEAR = "DELETE FROM users";
}
