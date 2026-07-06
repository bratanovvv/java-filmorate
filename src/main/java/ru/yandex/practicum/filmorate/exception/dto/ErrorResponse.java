package ru.yandex.practicum.filmorate.exception.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class ErrorResponse {
    private String path;
    private String message;
    private Instant timestamp;
}