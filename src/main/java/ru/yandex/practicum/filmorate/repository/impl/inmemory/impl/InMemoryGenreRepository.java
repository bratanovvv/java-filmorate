package ru.yandex.practicum.filmorate.repository.impl.inmemory.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;
import ru.yandex.practicum.filmorate.repository.impl.inmemory.InMemoryAbstractRepository;

import java.util.Comparator;
import java.util.List;

@Repository
@Profile("inmemory")
public class InMemoryGenreRepository extends InMemoryAbstractRepository<Integer, Genre> implements GenreRepository {

    @PostConstruct
    public void init() {
        saveGenre(1, "Комедия");
        saveGenre(2, "Драма");
        saveGenre(3, "Мультфильм");
        saveGenre(4, "Триллер");
        saveGenre(5, "Документальный");
        saveGenre(6, "Боевик");
    }

    private void saveGenre(int id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);
        repository.put(id, genre);
    }

    @Override
    public List<Genre> getAll() {
        return repository.values().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    @Override
    public Genre save(Genre genre) {
        repository.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        repository.put(genre.getId(), genre);
        return genre;
    }
}