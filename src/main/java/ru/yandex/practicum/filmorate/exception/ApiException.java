package ru.yandex.practicum.filmorate.exception;

public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;

    public ApiException(ErrorCode code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public ErrorCode getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}