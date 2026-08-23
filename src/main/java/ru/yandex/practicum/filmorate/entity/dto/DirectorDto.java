package ru.yandex.practicum.filmorate.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationKeys;

@Data
public class DirectorDto {
    @NotNull(groups = ValidationGroups.Update.class,
            message = ValidationKeys.DIRECTOR_ID_NOT_NULL)
    private Integer id;
    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
            message = ValidationKeys.DIRECTOR_NAME_NOT_BLANK)
    private String name;
}
