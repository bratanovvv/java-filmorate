package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.AbstractRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FilmRepository extends AbstractRepository<Integer, Film> {
    private final AtomicInteger nextId = new AtomicInteger(1);
    @Override
    public Film save(Film film) {
        if(film.getId() == null) {
            film.setId(nextId.getAndIncrement());
        }
        repository.put(film.getId(), film);
        return film;
    }
}
