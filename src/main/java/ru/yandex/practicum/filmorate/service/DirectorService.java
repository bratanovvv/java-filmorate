package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.dao.Director;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.repository.impl.DirectorRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    public Director getDirector(int id) {
        return directorRepository.getById(id)
                .orElseThrow(() ->
                        new ApiException(ErrorCode.DIRECTOR_NOT_FOUND, id));
    }

    public List<Director> getDirectors() {
        return directorRepository.getAll();
    }

    @Transactional
    public Director saveDirector(Director director) {
        Director saved = directorRepository.save(director);
        log.info("Создан режиссер: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public Director updateDirector(Director director) {
        Director existingDirector = getDirector(director.getId());

        existingDirector.setName(director.getName());

        Director updated = directorRepository.update(existingDirector);
        log.info("Обновлён режиссер: id={}, name={}", updated.getId(), updated.getName());

        return updated;
    }

    @Transactional
    public void deleteDirector(int id) {
        Director director = getDirector(id);
        directorRepository.delete(director.getId());
        log.info("Удален режиссер: id={}, name={}", director.getId(), director.getName());
    }
}
