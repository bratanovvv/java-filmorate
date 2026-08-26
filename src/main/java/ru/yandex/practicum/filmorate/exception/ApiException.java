package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;

    /**
     * Создаёт исключение с кодом ошибки и аргументами для подстановки в сообщение.
     */
    public ApiException(ErrorCode code, Object... args) {
        this.code = code;
        this.args = args;
    }
}