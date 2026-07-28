package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

/**
 * <summary>
 * Исключение, выбрасываемое при нарушении правил валидации объекта-значения (Value Object).
 * Указывает на то, что переданные данные не соответствуют внутренним требованиям
 * бизнес-логики для конкретного типа данных.
 * Обычно транслируется в статус ответа HTTP 400 (Bad Request).
 * </summary>
 **/
public final class ValueObjectIsInvalidException extends ValueObjectException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public ValueObjectIsInvalidException()
    {
        super();
    }

    public ValueObjectIsInvalidException(String message)
    {
        super(message);
    }

    public ValueObjectIsInvalidException(String message, String paramName)
    {
        super(message, paramName);
    }

    public ValueObjectIsInvalidException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ValueObjectIsInvalidException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}