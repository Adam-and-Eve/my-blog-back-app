package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

/**
 * <summary>
 * Юнит-тесты для проверки доменных правил и инвариантов PostTitleValueObject.
 * </summary>
 **/
class PostTitleValueObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание объекта-значения при передаче корректной строки.
     * </summary>
     **/
    @Test
    void constructorShouldCreateInstanceWhenValueIsValid()
    {
        var rawTitle = "Чистая архитектура и DDD";

        var titleVo = new PostTitleValueObject(rawTitle);

        Assertions.assertNotNull(titleVo);

        Assertions.assertEquals(rawTitle, titleVo.getValue());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если заголовок передан как null.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsNull()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new PostTitleValueObject(null)
        );

        Assertions.assertEquals("Заголовок не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если заголовок является пустой строкой.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsEmpty()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new PostTitleValueObject("")
        );

        Assertions.assertEquals("Заголовок не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет корректность работы методов сравнения equals и вычисления хэш-кода hashCode.
     * </summary>
     **/
    @Test
    void equalsAndHashCodeShouldVerifyValueEquality()
    {
        var firstTitle = new PostTitleValueObject("Тестовый заголовок");

        var secondTitle = new PostTitleValueObject("Тестовый заголовок");

        var differentTitle = new PostTitleValueObject("Совсем другой заголовок");

        Assertions.assertEquals(firstTitle, firstTitle);

        Assertions.assertEquals(firstTitle, secondTitle);

        Assertions.assertEquals(secondTitle, firstTitle);

        Assertions.assertEquals(firstTitle.hashCode(), secondTitle.hashCode());

        Assertions.assertNotEquals(firstTitle, differentTitle);

        Assertions.assertNotEquals(firstTitle, null);

        Assertions.assertNotEquals(firstTitle, "Просто строковый объект");
    }

    /**
     * <summary>
     * Проверяет, что метод toString возвращает внутреннее строковое значение заголовка.
     * </summary>
     **/
    @Test
    void toStringShouldReturnRawStringValue()
    {
        var rawTitle = "Заголовок для проверки строкового представления";

        var titleVo = new PostTitleValueObject(rawTitle);

        Assertions.assertEquals(rawTitle, titleVo.toString());
    }
}