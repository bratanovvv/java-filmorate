package ru.yandex.practicum.filmorate.repository.impl.inmemory.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;
import ru.yandex.practicum.filmorate.repository.impl.inmemory.InMemoryAbstractRepository;

import java.util.Comparator;
import java.util.List;

@Repository
@Profile("inmemory")
public class InMemoryFilmRepository extends InMemoryAbstractRepository<Integer, Film> implements FilmRepository {

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            film.setId(nextId.getAndIncrement());
        }
        repository.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        repository.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return repository.values().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
