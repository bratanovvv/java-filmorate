package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // FILM
    FILM_NOT_FOUND("film.notFound", HttpStatus.NOT_FOUND.value()),

    // USER
    USER_NOT_FOUND("user.notFound", HttpStatus.NOT_FOUND.value()),

    // GENRE
    GENRE_NOT_FOUND("genre.notFound", HttpStatus.NOT_FOUND.value()),

    // MPA_RATING
    MPA_RATING_NOT_FOUND("mpaRating.notFound", HttpStatus.NOT_FOUND.value()),

    DIRECTOR_NOT_FOUND("director.notFound", HttpStatus.NOT_FOUND.value()),

    // INTERNAL
    INSERT_FAILED("error.insertFailed", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    UPDATE_FAILED("error.updateFailed", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    DELETE_FAILED("error.deleteFailed", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    INTERNAL_SERVER_ERROR("internal.serverError", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    // REVIEW
    REVIEW_NOT_FOUND("review.notFound", HttpStatus.NOT_FOUND.value()),

    // SEARCH
    SEARCH_QUERY_EMPTY("search.query.empty", HttpStatus.BAD_REQUEST.value()),

    SEARCH_BY_INVALID("search.by.invalid", HttpStatus.BAD_REQUEST.value());


    private final String key;
    private final int httpStatus;

    /**
     * Создаёт константу из i18n-ключа сообщения и HTTP-статуса.
     */
    ErrorCode(String key, int httpStatus) {
        this.key = key;
        this.httpStatus = httpStatus;
    }
}
