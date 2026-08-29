package ru.yandex.practicum.filmorate.entity.dao;

import lombok.Data;

@Data
public class Review {
    private Integer id;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful;
}