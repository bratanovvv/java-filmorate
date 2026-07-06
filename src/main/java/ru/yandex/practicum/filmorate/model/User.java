package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.yandex.practicum.filmorate.constants.ValidationKeys;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;

    @Email(message = ValidationKeys.USER_EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationKeys.USER_LOGIN_NOT_BLANK)
    @Pattern(regexp = "^\\S+$", message = ValidationKeys.USER_LOGIN_NO_SPACES)
    private String login;

    private String name;

    @PastOrPresent(message = ValidationKeys.USER_BIRTHDAY_PAST_OR_PRESENT)
    private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    private Set<Integer> friends = new HashSet<>();
}
