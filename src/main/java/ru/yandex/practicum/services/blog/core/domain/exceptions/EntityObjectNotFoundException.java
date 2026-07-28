package ru.yandex.practicum.services.blog.core.domain.exceptions;

import java.io.Serial;

/**
 * <summary>
 * Исключение, выбрасываемое при попытке обращения к несуществующему объекту сущности.
 * Сигнализирует о том, что запрашиваемый ресурс (Entity Object) не найден в хранилище.
 * Обычно транслируется в статус ответа HTTP 404 (Not Found) на уровне API.
 * </summary>
 **/
public final class EntityObjectNotFoundException extends EntityObjectException
{
    // region Fields

    /**
     * Уникальный идентификатор версии сериализованного класса.
     **/
    @Serial
    private static final long serialVersionUID = 1L;

    // endregion

    // region Constructors

    public EntityObjectNotFoundException()
    {
        super();
    }

    public EntityObjectNotFoundException(String message)
    {
        super(message);
    }

    public EntityObjectNotFoundException(String message, String paramName)
    {
        super(message, paramName);
    }

    public EntityObjectNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityObjectNotFoundException(String message, String paramName, Throwable cause)
    {
        super(message, paramName, cause);
    }

    // endregion

    // region Properties



    // endregion

    // region Methods



    // endregion
}