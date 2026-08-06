package ru.yandex.practicum.filmorate.repository.impl.inmemory;

import ru.yandex.practicum.filmorate.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class InMemoryAbstractRepository<K, V> implements Repository<K, V> {

    protected final Map<K, V> repository = new HashMap<>();
    protected final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public Optional<V> getById(K id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public abstract V save(V t);

    @Override
    public abstract V update(V t);

    @Override
    public List<V> findAllByIds(Collection<K> ids) {
        return ids.stream()
                .map(repository::get)
                .filter(Objects::nonNull)
                .toList();
    }
}