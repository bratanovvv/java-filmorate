package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Repository<K, V> {
    Optional<V> getById(K id);

    List<V> getAll();

    List<V> findAllByIds(Collection<K> ids);

    V save(V entity);

    V update(V entity);

    void delete(K id);
}