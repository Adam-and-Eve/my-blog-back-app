package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

/**
 * <summary>
 * Исключение, выбрасываемое при попытке создания сущности, которая уже существует.
 * Сигнализирует о конфликте данных (например, нарушение уникальности полей)
 * или дублировании ресурсов.
 * Обычно транслируется в статус ответа HTTP 409 (Conflict).
 * </summary>
 **/
public final class EntityObjectAlreadyExistsException extends EntityObjectException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public EntityObjectAlreadyExistsException()
    {
        super();
    }

    public EntityObjectAlreadyExistsException(String message)
    {
        super(message);
    }

    public EntityObjectAlreadyExistsException(String message, String paramName)
    {
        super(message, paramName);
    }

    public EntityObjectAlreadyExistsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityObjectAlreadyExistsException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}