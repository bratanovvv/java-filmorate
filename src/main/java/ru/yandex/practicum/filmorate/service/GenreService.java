package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ApiException;
import ru.yandex.practicum.filmorate.exception.ErrorCode;
import ru.yandex.practicum.filmorate.entity.dao.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public Genre getGenre(int id) {
        return genreRepository.getById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.GENRE_NOT_FOUND, id));
    }

    public List<Genre> getGenres() {
        return genreRepository.getAll();
    }
}
