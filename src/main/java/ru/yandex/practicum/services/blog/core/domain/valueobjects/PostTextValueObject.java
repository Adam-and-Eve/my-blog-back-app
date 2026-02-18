package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import ru.yandex.practicum.services.blog.core.domain.entityobjects.base.BaseEntityObject;
import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;

import java.util.Objects;

/**
 * <summary>
 * Объект-значение (Value Object), представляющий сообщение публикации.
 * Инкапсулирует строковое значение сообщения и гарантирует его валидность
 * в соответствии с инвариантами доменной модели.
 * </summary>
 **/
public class PostTextValueObject extends BaseEntityObject
{
    // region Fields

    /**
     * Строковое значение сообщения публикации.
     **/
    private final String value;

    // endregion

    // region Constructors

    public PostTextValueObject(final String value)
    {
        this.value = isValid(value);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает строковое значение сообщения публикации.
     * </summary>
     * <return>
     * @return Значение сообщения.
     * </return>
     **/
    public final  String getValue()
    {
        return value;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Проверяет корректность переданного значения сообщения.
     * </summary>
     * @param value Проверяемое значение.
     * @return Валидное значение сообщения.
     * @throws ValueObjectIsInvalidException
     * Выбрасывается, если значение null или пустое.
     **/
    private static String isValid(final String value)
    {
        if (value == null ||
                value.isEmpty())
        {
            throw new ValueObjectIsInvalidException(
                    "Сообщение не может быть пустым",
                    "title");
        }
        else
        {
            return value;
        }
    }

    /**
     * <summary>
     * Сравнивает текущий экземпляр с другим объектом по значению сообщения.
     * </summary>
     * @param obj Объект для сравнения.
     * @return true, если значения заголовков равны; иначе false.
     **/
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof PostTextValueObject other)
        {
            return Objects.equals(value, other.value);
        }

        return false;
    }

    /**
     * <summary>
     * Вычисляет хэш-код на основе значения сообщения.
     * </summary>
     * @return Целочисленное значение хэш-кода.
     **/
    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

    /**
     * <summary>
     * Возвращает строковое представление объекта.
     * </summary>
     * @return Строковое значение сообщения.
     **/
    @Override
    public String toString()
    {
        return value;
    }

    // endregion
}