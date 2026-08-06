package ru.yandex.practicum.filmorate.repository.impl.db.query;

public final class GenreQueries {

    private GenreQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM genres ORDER BY id";
    public static final String FIND_BY_ID = "SELECT * FROM genres WHERE id = ?";
    public static final String FIND_ALL_BY_IDS = "SELECT * FROM genres WHERE id IN (%s)";
}