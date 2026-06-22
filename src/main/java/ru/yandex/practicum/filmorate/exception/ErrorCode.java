package ru.yandex.practicum.filmorate.exception;

public enum ErrorCode {

    // FILM
    FILM_NOT_FOUND("film.notFound", 404),

    // USER
    USER_NOT_FOUND("user.notFound", 404);

    private final String key;
    private final int httpStatus;

    ErrorCode(String key, int httpStatus) {
        this.key = key;
        this.httpStatus = httpStatus;
    }

    public String key() {
        return key;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
