package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<K, V> {
    Optional<V> getById(K id);

    List<V> getAll();

    V save(V t);

    void clear();
}
