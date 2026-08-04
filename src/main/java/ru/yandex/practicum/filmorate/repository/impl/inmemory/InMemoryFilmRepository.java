package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

@Component
public class InMemoryFilmRepository extends InMemoryAbstractRepository<Integer, Film> implements FilmRepository {

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            film.setId(nextId.getAndIncrement());
        }
        repository.put(film.getId(), film);
        return film;
    }
}
