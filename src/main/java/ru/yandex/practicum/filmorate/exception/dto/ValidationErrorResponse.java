package ru.yandex.practicum.filmorate.exception.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fields;
}
