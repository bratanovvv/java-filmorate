package ru.yandex.practicum.filmorate.entity.dao;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    private Set<Integer> friends = new LinkedHashSet<>();
}
