package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

/**
 * <summary>
 * Исключение доменного уровня, выбрасываемое при нарушении
 * допустимого состояния сущности (Entity Object).
 * </summary>
 **/
public class EntityObjectIllegalStateException extends EntityObjectException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public EntityObjectIllegalStateException()
    {
        super();
    }

    public EntityObjectIllegalStateException(String message)
    {
        super(message);
    }

    public EntityObjectIllegalStateException(String message, String paramName)
    {
        super(message, paramName);
    }

    public EntityObjectIllegalStateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityObjectIllegalStateException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}