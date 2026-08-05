package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Film;
import ru.yandex.practicum.filmorate.repository.impl.FilmRepository;

@Component
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
}
