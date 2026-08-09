package ru.yandex.practicum.filmorate.repository.impl.inmemory.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;
import ru.yandex.practicum.filmorate.repository.impl.inmemory.InMemoryAbstractRepository;

import java.util.Comparator;
import java.util.List;

@Repository
@Profile("inmemory")
public class InMemoryMpaRatingRepository extends InMemoryAbstractRepository<Integer, MpaRating> implements MpaRatingRepository {

    @PostConstruct
    public void init() {
        saveRating(1, "G");
        saveRating(2, "PG");
        saveRating(3, "PG-13");
        saveRating(4, "R");
        saveRating(5, "NC-17");
    }

    private void saveRating(int id, String name) {
        MpaRating rating = new MpaRating();
        rating.setId(id);
        rating.setName(name);
        repository.put(id, rating);
    }

    @Override
    public List<MpaRating> getAll() {
        return repository.values().stream()
                .sorted(Comparator.comparing(MpaRating::getId))
                .toList();
    }

    @Override
    public MpaRating save(MpaRating rating) {
        repository.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public MpaRating update(MpaRating rating) {
        repository.put(rating.getId(), rating);
        return rating;
    }
}