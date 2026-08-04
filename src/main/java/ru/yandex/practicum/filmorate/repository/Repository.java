package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Repository<K, V> {
    Optional<V> getById(K id);

    List<V> getAll();

    void clear();

    V save(V entity);

    List<V> findAllByIds(Collection<K> ids);
}