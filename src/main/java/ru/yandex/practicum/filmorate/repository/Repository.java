package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<ID, T> {
    Optional<T> getById(ID id);

    List<T> getAll();

    T save(T t);
}
