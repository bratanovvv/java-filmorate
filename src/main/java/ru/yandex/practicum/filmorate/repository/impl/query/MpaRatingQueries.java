package ru.yandex.practicum.filmorate.repository.impl.query;

public final class MpaRatingQueries {

    private MpaRatingQueries() {
    }

    public static final String FIND_ALL = "SELECT * FROM mpa_ratings ORDER BY id";
    public static final String FIND_BY_ID = "SELECT * FROM mpa_ratings WHERE id = ?";
    public static final String FIND_ALL_BY_IDS = "SELECT * FROM mpa_ratings WHERE id IN (%s)";
}