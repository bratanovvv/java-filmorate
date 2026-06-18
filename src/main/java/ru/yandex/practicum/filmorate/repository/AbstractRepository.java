package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRepository<ID, T> implements Repository<ID, T> {

    protected final Map<ID, T> repository = new HashMap<>();

    @Override
    public Optional<T> getById(ID id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(repository.values());
    }
}
