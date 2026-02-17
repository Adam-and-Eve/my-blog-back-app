package ru.yandex.practicum.services.blog.core.domain.valueobjects.base;

/**
 * <summary>
 * Базовый абстрактный класс для всех объектов-значений (Value Objects).
 * Объекты-значения не имеют уникального идентификатора и определяются набором своих свойств.
 * Все наследники должны быть неизменяемыми (immutable).
 * </summary>
 **/
public abstract class BaseValueObject
{
    // region Fields



    // endregion

    // region Constructors

    protected BaseValueObject()
    {

    }

    // endregion

    // region Properties



    // endregion

    // region Methods

    /**
     * <summary>
     * Определяет равенство текущего объекта-значения с другим объектом.
     * </summary>
     * @param obj Объект для сравнения.
     * @return true, если все свойства объектов идентичны.
     **/
    @Override
    public abstract boolean equals(final Object obj);

    /**
     * <summary>
     * Вычисляет хэш-код объекта на основе всех его значимых свойств.
     * </summary>
     * @return Хэш-код объекта.
     **/
    @Override
    public abstract int hashCode();

    /**
     * <summary>
     * Возвращает строковое представление объекта-значения.
     * </summary>
     * @return Строковое представление данных.
     **/
    @Override
    public abstract String toString();

    // endregion
}