package ru.yandex.practicum.services.blog.core.domain.entityobjects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.TagNameObject;

/**
 * <summary>
 * Юнит-тесты для проверки доменной логики и мутаций состояния TagEntityObject.
 * </summary>
 **/
public final class TagEntityObjectTest
{
    /**
     * <summary>
     * Проверяет успешное создание новой сущности тега с автоматической генерацией ID.
     * </summary>
     **/
    @Test
    void constructorShouldCreateNewInstanceWhenNameIsValid()
    {
        var tagName = new TagNameObject("java");

        var tagEntity = new TagEntityObject(tagName);

        Assertions.assertNotNull(tagEntity);

        Assertions.assertEquals(tagName, tagEntity.getName());
    }

    /**
     * <summary>
     * Проверяет успешное воссоздание существующей сущности тега с указанным идентификатором.
     * </summary>
     **/
    @Test
    void constructorWithIdShouldRestoreInstanceCorrectly()
    {
        var expectedId = 42L;

        var tagName = new TagNameObject("spring");

        var tagEntity = new TagEntityObject(expectedId, tagName);

        Assertions.assertNotNull(tagEntity);

        Assertions.assertEquals(expectedId, tagEntity.getId());

        Assertions.assertEquals(tagName, tagEntity.getName());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException в конструкторе новой сущности, если передан null.
     * </summary>
     **/
    @Test
    void constructorShouldThrowNullPointerExceptionWhenNameIsNull()
    {
        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> new TagEntityObject(null)
        );

        Assertions.assertEquals("Объект тега не может быть null.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException в конструкторе существующей сущности, если передан null.
     * </summary>
     **/
    @Test
    void constructorWithIdShouldThrowNullPointerExceptionWhenNameIsNull()
    {
        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> new TagEntityObject(1L, null)
        );

        Assertions.assertEquals("Объект тега не может быть null.", exception.getMessage());
    }

    /**
     * <summary>
     * Проверяет, что метод changeName успешно обновляет имя тега, если передано новое значение.
     * </summary>
     **/
    @Test
    void changeNameShouldUpdateValueWhenNewNameIsDifferent()
    {
        var oldName = new TagNameObject("docker");

        var newName = new TagNameObject("kubernetes");

        var tagEntity = new TagEntityObject(oldName);

        tagEntity.changeName(newName);

        Assertions.assertEquals(newName, tagEntity.getName());
    }

    /**
     * <summary>
     * Проверяет, что метод changeName игнорирует обновление, если переданное имя эквивалентно текущему.
     * </summary>
     **/
    @Test
    void changeNameShouldNotModifyStateWhenNewNameIsEqualToCurrent()
    {
        var tagName = new TagNameObject("postgres");

        var identicalName = new TagNameObject("postgres");

        var tagEntity = new TagEntityObject(tagName);

        tagEntity.changeName(identicalName);

        Assertions.assertEquals(tagName, tagEntity.getName());
    }

    /**
     * <summary>
     * Проверяет выброс NullPointerException при попытке передать null в метод changeName.
     * </summary>
     **/
    @Test
    void changeNameShouldThrowNullPointerExceptionWhenNewNameIsNull()
    {
        var tagName = new TagNameObject("ci-cd");

        var tagEntity = new TagEntityObject(tagName);

        var exception = Assertions.assertThrows(
                NullPointerException.class,
                () -> tagEntity.changeName(null)
        );

        Assertions.assertEquals("Объект тега не может быть null.", exception.getMessage());
    }
}