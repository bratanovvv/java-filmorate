package ru.yandex.practicum.filmorate.entity.mapper;

public interface Mapper <F, T> {

    T toEntity(F f);

    F toDto(T t);

}
