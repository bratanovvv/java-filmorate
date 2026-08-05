package ru.yandex.practicum.filmorate.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationKeys;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;

import java.time.LocalDate;

@Data
public class UserDto {

    private Integer id;

    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
           message = ValidationKeys.USER_EMAIL_INVALID)
    private String email;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
              message = ValidationKeys.USER_LOGIN_NOT_BLANK)
    @Pattern(regexp = "^\\S+$", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
             message = ValidationKeys.USER_LOGIN_NO_SPACES)
    private String login;

    private String name;

    @PastOrPresent(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class},
                   message = ValidationKeys.USER_BIRTHDAY_PAST_OR_PRESENT)
    private LocalDate birthday;
}
