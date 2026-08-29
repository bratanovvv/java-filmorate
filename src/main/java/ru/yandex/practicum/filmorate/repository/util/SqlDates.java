package ru.yandex.practicum.filmorate.repository.util;

import lombok.experimental.UtilityClass;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Конвертация между {@link LocalDate} и {@link java.sql.Date} с учётом {@code null}.
 */
@UtilityClass
public class SqlDates {

    /**
     * Преобразует {@code LocalDate} в {@code java.sql Date}; возвращает {@code null} для {@code null}.
     */
    public Date toSqlDate(LocalDate date) {
        return date != null ? Date.valueOf(date) : null;
    }

    /**
     * Преобразует {@code java.sql.Date} в {@code LocalDate}; возвращает {@code null} для {@code null}.
     */
    public LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }
}
