package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.MpaRating;
import ru.yandex.practicum.filmorate.repository.impl.MpaRatingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
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
    public Optional<MpaRating> getById(Integer id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<MpaRating> getAll() {
        return repository.values().stream()
                .sorted((r1, r2) -> Integer.compare(r1.getId(), r2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        repository.clear();
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

    @Override
    public List<MpaRating> findAllByIds(Collection<Integer> ids) {
        return ids.stream()
                .map(repository::get)
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }
}
