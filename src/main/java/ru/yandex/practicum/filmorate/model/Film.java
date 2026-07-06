package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.yandex.practicum.filmorate.constants.ValidationKeys;
import ru.yandex.practicum.filmorate.model.validation.annotation.MinDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;

    @NotBlank(message = ValidationKeys.FILM_NAME_NOT_BLANK)
    private String name;

    @Size(max = 200, message = ValidationKeys.FILM_DESCRIPTION_TOO_LONG)
    private String description;

    @MinDate(value = "1895-12-18", message = ValidationKeys.FILM_RELEASE_MIN_DATE)
    private LocalDate releaseDate;

    @Positive(message = ValidationKeys.FILM_DURATION_POSITIVE)
    private int duration;

    @Setter(AccessLevel.NONE)
    private Set<Integer> likes = new HashSet<>();
}
