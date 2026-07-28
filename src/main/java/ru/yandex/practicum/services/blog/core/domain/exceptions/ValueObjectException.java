package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

import ru.yandex.practicum.services.blog.core.domain.exceptions.base.BaseDomainException;

/**
 * <summary>
 * Базовое исключение для ошибок, связанных с объектами-значениями (Value Objects).
 * Возникает при попытке создания или изменения объекта-значения с некорректными данными,
 * гарантируя соблюдение инвариантов бизнес-логики на самом базовом уровне.
 * </summary>
 **/
public abstract class ValueObjectException extends BaseDomainException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public ValueObjectException()
    {
        super();
    }

    public ValueObjectException(String message)
    {
        super(message);
    }

    public ValueObjectException(String message, String paramName)
    {
        super(message, paramName);
    }

    public ValueObjectException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ValueObjectException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}