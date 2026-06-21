package ru.yandex.practicum.filmorate.model.base;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Базовый класс для тестирования Bean Validation сущностей.
 *
 * <p>Предоставляет общую инфраструктуру для проверки Jakarta Validation, включая создание валидатора и базовые assertions.</p>
 *
 * @param <T> тип валидируемой сущности
 */
public abstract class BaseValidationEntityTest<T> {

    /**
     * Общий Hibernate Validator для всех тестов.
     */
    protected static Validator validator;

    /**
     * Инициализация ValidatorFactory перед запуском всех тестов.
     */
    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Создаёт корректную (валидную) сущность для тестов.
     *
     * @return валидный объект типа T
     */
    protected abstract T createValidEntity();

    /**
     * Выполняет валидацию объекта.
     *
     * @param object объект для проверки
     * @return набор нарушений ограничений
     */
    protected Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

    /**
     * Проверяет, что объект полностью валиден (нет нарушений).
     *
     * @param object объект для проверки
     */
    protected void assertValid(T object) {
        assertTrue(validate(object).isEmpty(),
                "Ожидалось отсутствие ошибок валидации");
    }

    /**
     * Проверяет, что у объекта есть нарушение валидации для указанного поля.
     *
     * @param object объект для проверки
     * @param field  имя поля (property path)
     */
    protected void assertViolationOnField(T object, String field) {

        Set<ConstraintViolation<T>> violations = validate(object);

        assertFalse(violations.isEmpty(),
                "Ожидалась ошибка валидации");

        boolean hasFieldViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));

        assertTrue(hasFieldViolation,
                "Ожидалась ошибка на поле: " + field);
    }
}