package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
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
    public Optional<Genre> getById(Integer id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<Genre> getAll() {
        return repository.values().stream()
                .sorted((g1, g2) -> Integer.compare(g1.getId(), g2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        repository.clear();
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

    @Override
    public List<Genre> findAllByIds(Collection<Integer> ids) {
        return ids.stream()
                .map(repository::get)
                .filter(g -> g != null)
                .collect(Collectors.toList());
    }
}
