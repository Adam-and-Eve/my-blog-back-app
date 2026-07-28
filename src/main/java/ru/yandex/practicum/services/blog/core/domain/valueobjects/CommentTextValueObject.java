package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.base.BaseValueObject;

import java.util.Objects;

/**
 * <summary>
 * Объект-значение (Value Object), представляющий сообщение комментария.
 * Инкапсулирует строковое значение сообщения и гарантирует его валидность
 * в соответствии с инвариантами доменной модели.
 * </summary>
 **/
public final class CommentTextValueObject extends BaseValueObject
{
    // region Fields

    /**
     * Строковое значение сообщения комментария.
     **/
    private final String value;

    // endregion

    // region Constructors

    public CommentTextValueObject(final String value)
    {
        this.value = isValid(value);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает строковое значение сообщения комментария.
     * </summary>
     * <return>
     * @return Значение сообщения комментария.
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
     * Проверяет корректность переданного значения сообщения комментария.
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
                    "Сообщение комментария не может быть пустым",
                    "title");
        }
        else
        {
            return value;
        }
    }

    /**
     * <summary>
     * Сравнивает текущий экземпляр с другим объектом по значению сообщения комментария.
     * </summary>
     * @param obj Объект для сравнения.
     * @return true, если значения сообщений комментариев равны; иначе false.
     **/
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof CommentTextValueObject other)
        {
            return Objects.equals(value, other.value);
        }

        return false;
    }

    /**
     * <summary>
     * Вычисляет хэш-код на основе значения сообщения комментария.
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
     * @return Строковое значение сообщения комментария.
     **/
    @Override
    public String toString()
    {
        return value;
    }

    // endregion
}