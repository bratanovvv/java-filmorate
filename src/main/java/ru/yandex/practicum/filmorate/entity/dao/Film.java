package ru.yandex.practicum.filmorate.entity.dao;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaRating mpa;

    @Setter(AccessLevel.NONE)
    private Set<Genre> genres = new LinkedHashSet<>();

    @Setter(AccessLevel.NONE)
    private Set<Director> directors = new LinkedHashSet<>();
}
