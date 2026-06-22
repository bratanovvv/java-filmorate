package ru.yandex.practicum.filmorate.model.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.model.validation.validator.MinDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Валидация даты.
 *
 * <p>Проверяет, что значение типа {@link java.time.LocalDate} не меньше указанной минимальной даты.</p>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * @MinDate("1895-12-18")
 * private LocalDate releaseDate;
 * }</pre>
 *
 * @see MinDateValidator
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinDateValidator.class)
public @interface MinDate {

    /**
     * Минимально допустимая дата в формате ISO (yyyy-MM-dd).
     */
    String value();

    String message() default "validator.date.isAfter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}