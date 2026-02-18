package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import java.util.Objects;

import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.base.BaseValueObject;

/**
 * <summary>
 * Объект-значение (Value Object), представляющий заголовок публикации.
 * Инкапсулирует строковое значение заголовка и гарантирует его валидность
 * в соответствии с инвариантами доменной модели.
 * </summary>
 **/
public final class PostTitleValueObject extends BaseValueObject
{
    // region Fields

    /**
     * Строковое значение заголовка публикации.
     **/
    private final String value;

    // endregion

    // region Constructors

    public PostTitleValueObject(final String value)
    {
        this.value = isValid(value);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает строковое значение заголовка публикации.
     * </summary>
     * <return>
     * @return Значение заголовка.
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
     * Проверяет корректность переданного значения заголовка.
     * </summary>
     * @param value Проверяемое значение.
     * @return Валидное значение заголовка.
     * @throws ValueObjectIsInvalidException
     * Выбрасывается, если значение null или пустое.
     **/
    private static String isValid(final String value)
    {
        if (value == null ||
            value.isEmpty())
        {
            throw new ValueObjectIsInvalidException(
                    "Заголовок не может быть пустым",
                    "title");
        }
        else
        {
            return value;
        }
    }

    /**
     * <summary>
     * Сравнивает текущий экземпляр с другим объектом по значению заголовка.
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

        if (obj instanceof PostTitleValueObject other)
        {
            return Objects.equals(value, other.value);
        }

        return false;
    }

    /**
     * <summary>
     * Вычисляет хэш-код на основе значения заголовка.
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
     * @return Строковое значение заголовка.
     **/
    @Override
    public String toString()
    {
        return value;
    }

    // endregion
}