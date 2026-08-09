package ru.yandex.practicum.filmorate.repository.impl;

import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.repository.Repository;

import java.util.List;

public interface FilmRepository extends Repository<Integer, Film> {

    List<Film> getPopularFilms(int count);
}