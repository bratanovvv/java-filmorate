package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.entity.dto.DirectorDto;
import ru.yandex.practicum.filmorate.entity.dto.validation.ValidationGroups;
import ru.yandex.practicum.filmorate.entity.mapper.Mapper;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;
    private final Mapper<DirectorDto, Director> directorMapper;

    @GetMapping("/{id}")
    public DirectorDto getDirector(@PathVariable int id) {
        Director director = directorService.getDirector(id);
        return directorMapper.toDto(director);
    }

    @GetMapping
    public List<DirectorDto> getDirectors() {
        return directorMapper.toDtoList(directorService.getDirectors());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DirectorDto createDirector(@Validated(ValidationGroups.Create.class) @RequestBody DirectorDto directorDto) {
        directorDto.setId(null);
        Director director = directorMapper.toEntity(directorDto);
        Director saved = directorService.saveDirector(director);
        return directorMapper.toDto(saved);
    }

    @PutMapping
    public DirectorDto updateDirector(@Validated(ValidationGroups.Update.class) @RequestBody DirectorDto directorDto) {
        Director director = directorMapper.toEntity(directorDto);
        Director updated = directorService.updateDirector(director);
        return directorMapper.toDto(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
    }
}
