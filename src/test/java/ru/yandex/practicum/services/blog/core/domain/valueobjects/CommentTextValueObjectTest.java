package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

/**
 * <summary>
 * Юнит-тесты для проверки доменных правил и инвариантов CommentTextValueObject.
 * </summary>
 **/
public final class CommentTextValueObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание объекта-значения при передаче корректного текста комментария.
     * </summary>
     **/
    @Test
    void constructorShouldCreateInstanceWhenValueIsValid()
    {
        var rawComment = "Отличная статья, спасибо за разбор!";

        var commentVo = new CommentTextValueObject(rawComment);

        Assertions.assertNotNull(commentVo);

        Assertions.assertEquals(rawComment, commentVo.getValue());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если текст комментария передан как null.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsNull()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new CommentTextValueObject(null)
        );

        Assertions.assertEquals("Сообщение комментария не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс ValueObjectIsInvalidException, если передан пустой текст комментария.
     * </summary>
     **/
    @Test
    void constructorShouldThrowValueObjectIsInvalidExceptionWhenValueIsEmpty()
    {
        var exception = Assertions.assertThrows(
                ValueObjectIsInvalidException.class,
                () -> new CommentTextValueObject("")
        );

        Assertions.assertEquals("Сообщение комментария не может быть пустым", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет корректность работы методов сравнения equals и вычисления хэш-кода hashCode.
     * </summary>
     **/
    @Test
    void equalsAndHashCodeShouldVerifyValueEquality()
    {
        var firstComment = new CommentTextValueObject("Интересная мысль");

        var secondComment = new CommentTextValueObject("Интересная мысль");

        var differentComment = new CommentTextValueObject("Не согласен с автором");

        Assertions.assertEquals(firstComment, firstComment);

        Assertions.assertEquals(firstComment, secondComment);

        Assertions.assertEquals(secondComment, firstComment);

        Assertions.assertEquals(firstComment.hashCode(), secondComment.hashCode());

        Assertions.assertNotEquals(firstComment, differentComment);

        Assertions.assertNotEquals(firstComment, null);

        Assertions.assertNotEquals(firstComment, new Object());
    }

    /**
     * <summary>
     * Проверяет, что метод toString возвращает исходное строковое значение комментария.
     * </summary>
     **/
    @Test
    void toStringShouldReturnRawStringValue()
    {
        var rawComment = "Тестовая строка для проверки toString";

        var commentVo = new CommentTextValueObject(rawComment);

        Assertions.assertEquals(rawComment, commentVo.toString());
    }
}