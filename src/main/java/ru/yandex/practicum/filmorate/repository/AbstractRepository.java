package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRepository<K, V> implements Repository<K, V> {

    protected final Map<K, V> repository = new HashMap<>();

    @Override
    public Optional<V> getById(K id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public void clear() {
        repository.clear();
    }
}
