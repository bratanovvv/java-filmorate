package ru.yandex.practicum.filmorate.constants;

public final class ValidationKeys {
    private ValidationKeys() {
    }

    // USER
    public static final String USER_LOGIN_NOT_BLANK = "{user.login.notBlank}";
    public static final String USER_LOGIN_NO_SPACES = "{user.login.noSpaces}";
    public static final String USER_BIRTHDAY_PAST_OR_PRESENT = "{user.birthday.pastOrPresent}";
    public static final String USER_EMAIL_INVALID = "{user.email.invalid}";

    // FILM
    public static final String FILM_NAME_NOT_BLANK = "{film.name.notBlank}";
    public static final String FILM_DESCRIPTION_TOO_LONG = "{film.description.tooLong}";
    public static final String FILM_RELEASE_MIN_DATE = "{film.releaseDate.min}";
    public static final String FILM_DURATION_POSITIVE = "{film.duration.positive}";
}
