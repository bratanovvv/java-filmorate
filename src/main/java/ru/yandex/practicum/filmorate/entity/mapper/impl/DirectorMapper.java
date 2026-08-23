package ru.yandex.practicum.filmorate.entity.mapper.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dto.DirectorDto;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;

@Component
public class DirectorMapper implements Mapper<DirectorDto, Director> {

    @Override
    public Director toEntity(DirectorDto dto) {
        if (dto == null) {
            return null;
        }
        Director entity = new Director();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public DirectorDto toDto(Director entity) {
        if (entity == null) {
            return null;
        }
        DirectorDto dto = new DirectorDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
