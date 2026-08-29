package ru.yandex.practicum.filmorate.entity.mapper;

import java.util.Collection;
import java.util.List;

public interface Mapper<F, T> {

    T toEntity(F f);

    F toDto(T t);

    /**
     * Преобразует коллекцию сущностей в список DTO.
     */
    default List<F> toDtoList(Collection<T> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

}
