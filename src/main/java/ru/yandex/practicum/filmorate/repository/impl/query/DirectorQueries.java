package ru.yandex.practicum.filmorate.repository.impl.query;

public final class DirectorQueries {
    private DirectorQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM directors ORDER BY id";
    public static final String FIND_BY_ID = "SELECT * FROM directors WHERE id = ?";
    public static final String FIND_ALL_BY_IDS = "SELECT * FROM directors WHERE id IN (%s)";

    public static final String INSERT = """
            INSERT INTO directors (name)
            VALUES (?)
            """;

    public static final String UPDATE = """
            UPDATE directors
            SET name = ?
            WHERE id = ?
            """;

    public static final String DELETE = """
            DELETE FROM directors WHERE id = ?
            """;
}
