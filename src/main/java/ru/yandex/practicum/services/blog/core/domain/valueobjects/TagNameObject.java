package ru.yandex.practicum.services.blog.core.domain.valueobjects;

import ru.yandex.practicum.services.blog.core.domain.exceptions.ValueObjectIsInvalidException;
import ru.yandex.practicum.services.blog.core.domain.valueobjects.base.BaseValueObject;

import java.util.Objects;

/**
 * <summary>
 * Объект-значение (Value Object), представляющий тег публикации.
 * Инкапсулирует строковое значение тега и гарантирует его валидность
 * в соответствии с инвариантами доменной модели.
 * </summary>
 **/
public final class TagNameObject extends BaseValueObject
{
    // region Fields

    /**
     * Строковое значение тега публикации.
     **/
    private final String value;

    // endregion

    // region Constructors

    public TagNameObject(final String value)
    {
        this.value = isValid(value);
    }

    // endregion

    // region Properties

    /**
     * <summary>
     * Возвращает строковое значение тега публикации.
     * </summary>
     * <return>
     * @return Значение тега.
     * </return>
     **/
    public final String getValue()
    {
        return value;
    }

    // endregion

    // region Methods

    /**
     * <summary>
     * Проверяет корректность переданного значения тега.
     * </summary>
     * @param value Проверяемое значение.
     * @return Валидное значение тега.
     * @throws ValueObjectIsInvalidException
     * Выбрасывается, если значение null или пустое.
     **/
    private static String isValid(final String value)
    {
        if (value == null ||
                value.isEmpty())
        {
            throw new ValueObjectIsInvalidException(
                    "Тег не может быть пустым",
                    "tag");
        }
        else
        {
            return value;
        }
    }

    /**
     * <summary>
     * Сравнивает текущий экземпляр с другим объектом по значению тега.
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

        if (obj instanceof TagNameObject other)
        {
            return Objects.equals(value, other.value);
        }

        return false;
    }

    /**
     * <summary>
     * Вычисляет хэш-код на основе значения тега.
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
     * @return Строковое значение тега.
     **/
    @Override
    public String toString()
    {
        return value;
    }

    // endregion
}
