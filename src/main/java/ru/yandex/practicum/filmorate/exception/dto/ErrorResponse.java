package ru.yandex.practicum.filmorate.exception.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
public class ErrorResponse {
    private String path;
    private String error;
    private Instant timestamp;
}