package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

/**
 * <summary>
 * Юнит-тесты для проверки доменных правил и инвариантов PostTextValueObject.
 * </summary>
 **/
public final class PostTextValueObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание объекта-значения при передаче корректного текста публикации.
     * </summary>
     **/
    @Test
    void constructorShouldCreateInstanceWhenValueIsValid()
    {
        var rawText = "В этой публикации мы детально рассмотрим основы проектирования доменной модели...";

        var textVo = new PostTextValueObject(rawText);

        Assertions.assertNotNull(textVo);

        Assertions.assertEquals(rawText, textVo.getValue());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если текст передан как null.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsNull()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new PostTextValueObject(null)
        );

        Assertions.assertEquals("Сообщение не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если передан пустой текст.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsEmpty()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new PostTextValueObject("")
        );

        Assertions.assertEquals("Сообщение не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет корректность работы методов равенства equals и вычисления хэш-кода hashCode.
     * </summary>
     **/
    @Test
    void equalsAndHashCodeShouldVerifyValueEquality()
    {
        var firstText = new PostTextValueObject("Контент публикации");

        var secondText = new PostTextValueObject("Контент публикации");

        var differentText = new PostTextValueObject("Совершенно другой контент");

        // Проверка идентичности ссылки
        Assertions.assertEquals(firstText, firstText);

        Assertions.assertEquals(firstText, secondText);

        Assertions.assertEquals(secondText, firstText);

        Assertions.assertEquals(firstText.hashCode(), secondText.hashCode());

        Assertions.assertNotEquals(firstText, differentText);

        Assertions.assertNotEquals(firstText, null);

        Assertions.assertNotEquals(firstText, new Object());
    }

    /**
     * <summary>
     * Проверяет, что метод toString возвращает исходное строковое значение контента.
     * </summary>
     **/
    @Test
    void toStringShouldReturnRawStringValue()
    {
        var rawText = "Тестовый текст для проверки метода toString";

        var textVo = new PostTextValueObject(rawText);

        Assertions.assertEquals(rawText, textVo.toString());
    }
}