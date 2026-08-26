package ru.yandex.practicum.filmorate.entity.dto.validation;

public final class ValidationKeys {
    private ValidationKeys() {
    }

    // USER
    public static final String USER_LOGIN_NOT_BLANK = "{user.login.notBlank}";
    public static final String USER_LOGIN_NO_SPACES = "{user.login.noSpaces}";
    public static final String USER_BIRTHDAY_PAST_OR_PRESENT = "{user.birthday.pastOrPresent}";
    public static final String USER_EMAIL_INVALID = "{user.email.invalid}";
    public static final String USER_ID_NOT_NULL = "{user.id.notNull}";

    // FILM
    public static final String FILM_NAME_NOT_BLANK = "{film.name.notBlank}";
    public static final String FILM_DESCRIPTION_TOO_LONG = "{film.description.tooLong}";
    public static final String FILM_RELEASE_MIN_DATE = "{film.releaseDate.min}";
    public static final String FILM_DURATION_POSITIVE = "{film.duration.positive}";
    public static final String FILM_ID_NOT_NULL = "{film.id.notNull}";

    // REVIEW
    public static final String REVIEW_ID_NOT_NULL = "{review.id.notNull}";
    public static final String REVIEW_CONTENT_NOT_BLANK = "{review.content.notBlank}";
    public static final String REVIEW_CONTENT_TOO_LONG = "{review.content.tooLong}";
    public static final String REVIEW_IS_POSITIVE_NOT_NULL = "{review.isPositive.notNull}";
    public static final String REVIEW_USER_ID_NOT_NULL = "{review.userId.notNull}";
    public static final String REVIEW_FILM_ID_NOT_NULL = "{review.filmId.notNull}";

    public static final String DIRECTOR_ID_NOT_NULL = "{director.id.notNull}";
    public static final String DIRECTOR_NAME_NOT_BLANK = "{director.name.notBlank}";
}
