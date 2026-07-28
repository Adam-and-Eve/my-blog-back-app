package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

/**
 * <summary>
 * Юнит-тесты для проверки доменных правил и инвариантов TagNameObject.
 * </summary>
 **/
public final class TagNameObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание объекта-значения при передаче корректного имени тега.
     * </summary>
     **/
    @Test
    void constructorShouldCreateInstanceWhenValueIsValid()
    {
        var rawTagName = "backend";

        var tagVo = new TagNameObject(rawTagName);

        Assertions.assertNotNull(tagVo);

        Assertions.assertEquals(rawTagName, tagVo.getValue());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если имя тега передано как null.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsNull()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new TagNameObject(null)
        );

        Assertions.assertEquals("Тег не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если передано пустое имя тега.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsEmpty()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new TagNameObject("")
        );

        Assertions.assertEquals("Тег не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет корректность работы методов сравнения equals и вычисления хэш-кода hashCode.
     * </summary>
     **/
    @Test
    void equalsAndHashCodeShouldVerifyValueEquality()
    {
        var firstTag = new TagNameObject("java");

        var secondTag = new TagNameObject("java");

        var differentTag = new TagNameObject("spring");

        Assertions.assertEquals(firstTag, firstTag);

        Assertions.assertEquals(firstTag, secondTag);

        Assertions.assertEquals(secondTag, firstTag);

        Assertions.assertEquals(firstTag.hashCode(), secondTag.hashCode());

        Assertions.assertNotEquals(firstTag, differentTag);

        // Проверка устойчивости при сравнении с null и другими типами
        Assertions.assertNotEquals(firstTag, null);

        Assertions.assertNotEquals(firstTag, "просто строка");
    }

    /**
     * <summary>
     * Проверяет, что метод toString возвращает точное строковое значение тега.
     * </summary>
     **/
    @Test
    void toStringShouldReturnRawStringValue()
    {
        var rawTagName = "docker";

        var tagVo = new TagNameObject(rawTagName);

        Assertions.assertEquals(rawTagName, tagVo.toString());
    }
}