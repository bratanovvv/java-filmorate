package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // FILM
    FILM_NOT_FOUND("film.notFound", HttpStatus.NOT_FOUND.value()),

    // USER
    USER_NOT_FOUND("user.notFound", HttpStatus.NOT_FOUND.value());

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
