package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

import ru.yandex.practicum.services.blog.core.domain.exceptions.base.BaseDomainException;

/**
 * <summary>
 * Базовое исключение для ошибок, связанных с объектами сущностей (Entity Objects).
 * Используется как фундамент для более специфичных ошибок домена, таких как
 * отсутствие объекта, нарушение его целостности или конфликты состояний.
 * </summary>
 **/
public abstract class EntityObjectException extends BaseDomainException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public EntityObjectException()
    {
        super();
    }

    public EntityObjectException(String message)
    {
        super(message);
    }

    public EntityObjectException(String message, String paramName)
    {
        super(message, paramName);
    }

    public EntityObjectException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityObjectException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}