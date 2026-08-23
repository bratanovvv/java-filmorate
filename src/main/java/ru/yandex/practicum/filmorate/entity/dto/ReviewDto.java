package ru.yandex.practicum.filmorate.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationKeys;

@Data
public class ReviewDto {

    @NotNull(groups = ValidationGroups.Update.class,
            message = ValidationKeys.REVIEW_ID_NOT_NULL)
    private Integer reviewId;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.REVIEW_CONTENT_NOT_BLANK)
    @Size(max = 1000, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.REVIEW_CONTENT_TOO_LONG)
    private String content;

    @JsonProperty("isPositive")
    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.REVIEW_IS_POSITIVE_NOT_NULL)
    private Boolean isPositive;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.REVIEW_USER_ID_NOT_NULL)
    private Integer userId;

    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.REVIEW_FILM_ID_NOT_NULL)
    private Integer filmId;

    private int useful;
}