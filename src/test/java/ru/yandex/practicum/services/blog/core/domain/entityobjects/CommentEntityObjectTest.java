package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.CommentTextValueObject;

import java.time.OffsetDateTime;

/**
 * <summary>
 * Юнит-тесты для проверки доменной логики и мутаций состояния CommentEntityObject.
 * </summary>
 **/
public final class CommentEntityObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание новой сущности комментария.
     * </summary>
     **/
    @Test
    void constructorShouldCreateNewInstanceWhenTextIsValid()
    {
        var commentText = new CommentTextValueObject("Информативный разбор, спасибо!");

        var commentEntity = new CommentEntityObject(commentText);

        Assertions.assertNotNull(commentEntity);

        Assertions.assertEquals(commentText, commentEntity.getText());
    }

    /**
     * <summary>
     * Проверяет успешное восстановление существующей сущности комментария со всеми системными полями.
     * </summary>
     **/
    @Test
    void constructorWithAllFieldsShouldRestoreInstanceCorrectly()
    {
        var expectedId = 123L;

        var commentText = new CommentTextValueObject("Старый комментарий из базы данных");

        var createdAt = OffsetDateTime.now().minusDays(1);

        var updatedAt = OffsetDateTime.now();

        var commentEntity = new CommentEntityObject(expectedId, commentText, createdAt, updatedAt);

        Assertions.assertNotNull(commentEntity);

        Assertions.assertEquals(expectedId, commentEntity.getId());

        Assertions.assertEquals(commentText, commentEntity.getText());

        Assertions.assertEquals(createdAt, commentEntity.getCreatedAt());

        Assertions.assertEquals(updatedAt, commentEntity.getUpdatedAt());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException в конструкторе новой сущности, если передан null вместо текста.
     * </summary>
     **/
    @Test
    void constructorShouldThrowNullPointerExceptionWhenTextIsNull()
    {
        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> new CommentEntityObject(null)
        );

        Assertions.assertEquals("Объект сообщения комментария не может быть null.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException в конструкторе существующей сущности, если передан null вместо текста.
     * </summary>
     **/
    @Test
    void constructorWithAllFieldsShouldThrowNullPointerExceptionWhenTextIsNull()
    {
        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> new CommentEntityObject(1L, null, OffsetDateTime.now(), OffsetDateTime.now())
        );

        Assertions.assertEquals("Объект сообщения комментария не может быть null.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет, что метод changeText успешно обновляет текст комментария, если передано новое значение.
     * </summary>
     **/
    @Test
    void changeTextShouldUpdateValueWhenNewTextIsDifferent()
    {
        var oldText = new CommentTextValueObject("Текст до редактирования");

        var newText = new CommentTextValueObject("Отредактированный текст комментария");

        var commentEntity = new CommentEntityObject(oldText);

        commentEntity.changeText(newText);

        Assertions.assertEquals(newText, commentEntity.getText());
    }

    /**
     * <summary>
     * Проверяет, что метод changeText не изменяет состояние, если переданный текст эквивалентен текущему.
     * </summary>
     **/
    @Test
    void changeTextShouldNotModifyStateWhenNewTextIsEqualToCurrent()
    {
        var commentText = new CommentTextValueObject("Одинаковый текст");

        var identicalText = new CommentTextValueObject("Одинаковый текст");

        var commentEntity = new CommentEntityObject(commentText);

        commentEntity.changeText(identicalText);

        Assertions.assertEquals(commentText, commentEntity.getText());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException при попытке передать null в метод changeText.
     * </summary>
     **/
    @Test
    void changeTextShouldThrowNullPointerExceptionWhenNewTextIsNull()
    {
        var commentText = new CommentTextValueObject("Обычный комментарий");

        var commentEntity = new CommentEntityObject(commentText);

        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> commentEntity.changeText(null)
        );

        Assertions.assertEquals("Объект сообщения комментария не может быть null.", exception.getMessage());
    }
}