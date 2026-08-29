package ru.yandex.practicum.filmorate.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationKeys;
import ru.yandex.practicum.filmorate.entity.dto.validation.annotation.MinDate;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class FilmDto {

    @NotNull(groups = ValidationGroups.Update.class,
              message = ValidationKeys.FILM_ID_NOT_NULL)
    private Integer id;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
              message = ValidationKeys.FILM_NAME_NOT_BLANK)
    private String name;

    @Size(max = 200, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
          message = ValidationKeys.FILM_DESCRIPTION_TOO_LONG)
    private String description;

    @MinDate(value = "1895-12-18", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
             message = ValidationKeys.FILM_RELEASE_MIN_DATE)
    private LocalDate releaseDate;

    @Positive(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
              message = ValidationKeys.FILM_DURATION_POSITIVE)
    private int duration;

    private MpaRatingDto mpa;

    private Set<GenreDto> genres = new LinkedHashSet<>();
    private Set<DirectorDto> directors = new LinkedHashSet<>();
}
