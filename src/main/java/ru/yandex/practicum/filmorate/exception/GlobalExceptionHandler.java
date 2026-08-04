package ru.yandex.practicum.filmorate.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.dto.ValidationErrorResponse;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private Locale locale() {
        return LocaleContextHolder.getLocale();
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handle(ApiException ex, HttpServletRequest request) {
        ErrorCode code = ex.getCode();

        ErrorResponse body = ErrorResponse.builder()
                .message(messageSource.getMessage(code.key(), ex.getArgs(), locale()))
                .timestamp(Instant.now())
                .path(String.format("%s %s", request.getMethod(), request.getRequestURI()))
                .build();

        log.warn("Ошибка приложения: {}", body.getMessage());
        return ResponseEntity.status(code.httpStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                             HttpServletRequest request) {
        Map<String, String> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> messageSource.getMessage(fieldError, locale()),
                        (existing, replacement) -> existing
                ));

        log.warn("Ошибка валидации: {}", fields);

        ValidationErrorResponse body = ValidationErrorResponse.builder()
                .message(messageSource.getMessage("error.validation", null, locale()))
                .path(String.format("%s %s", request.getMethod(), request.getRequestURI()))
                .fields(fields)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Внутренняя ошибка приложения: ", ex);

        ErrorResponse body = ErrorResponse.builder()
                .message(messageSource.getMessage("error.internal", null, locale()))
                .path(String.format("%s %s", request.getMethod(), request.getRequestURI()))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.internalServerError().body(body);
    }
}